#!/bin/bash

TESTFILES_MD5SUM=05ee4e769bce91980ecf689f990f32cd
TESTFILES_TARBALL_FILENAME=nanoP2P_testfiles.tar.gz

#sudo apt-get install xterm wget
xterm -version > /dev/null 2>&1
if [ "$?" -ne 0 ]; then
    echo "This script requires 'xterm' installed to work"
    echo "In Ubuntu systems, run: 'sudo apt-get install xterm' to install it"
    exit
fi
wget --version > /dev/null 2>&1
if [ "$?" -ne 0 ]; then
    echo "This script requires 'wget' installed to work"
    echo "In Ubuntu systems, run: 'sudo apt-get install wget' to install it"
    exit
fi

# Check required number of arguments
SCRIPT_BASENAME=`basename $0`
if [ "$#" -lt 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: ${SCRIPT_BASENAME} <jar_file> <remote_host> <test_type>"
    echo "  <jar_file> is the runnable JAR file with your Peer program"
    echo "  <remote_host> is the hostname (or IP) of a host where $LOGNAME can login via SSH"
    echo "  <test_type> is the type of test ('basic' or 'advanced')"
    exit
fi
PEER_JARFILE_BASENAME=`basename $1`

# Check if jar file exists
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo -n "Running test script in $SCRIPT_DIR, hostname is "
echo `hostname`
if [ ! -f ${SCRIPT_DIR}/$1 ]; then
    echo "Cannot find '$1' JAR file in directory $SCRIPT_DIR "
    echo "You must export your Peer Java application as a JAR file and place it in the same directory as this script (currently, $SCRIPT_DIR)"
    exit
fi

TEST_TYPE=0

PEER_JARFILE=${SCRIPT_DIR}/$1
if [ "$#" -eq 4 ]; then
    if [ "$4" != "remote" ]; then
	# Hack to tell the script that we're executing in the remote host
	echo "Wrong arguments!"
	exit
    fi
    REMOTE_MODE=1
    TESTFILES_PATH=$HOME
    TRACKER_IP=$2
    echo "Remote mode"
elif [ "$#" -eq 3 ]; then    
    REMOTE_MODE=0
    REMOTE_HOST=$2
    TRACKER_IP=`hostname --all-ip-addresses | cut -d " " -f1`
    TESTFILES_PATH=/tmp
else
    echo "Too many arguments!"
    exit
fi

TEST_TYPE_STRING=$3
if [ $TEST_TYPE_STRING = "basic" ]; then
    TEST_TYPE=0
elif [ $TEST_TYPE_STRING = "advanced" ]; then
    TEST_TYPE=1
else
    echo "Wrong test type! Choose between 'basic' or 'advanced' "
    exit
fi

# Download tarball containing test files and tracker.jar
if [ $REMOTE_MODE -eq 0 ]; then # Only the local box downloads test files
    cd ${TESTFILES_PATH}
    while true ; do
	if [ ! -f ${TESTFILES_PATH}/${TESTFILES_TARBALL_FILENAME} ]; then
	    echo "Downloading test files..."
	    wget jumilla.inf.um.es/${TESTFILES_TARBALL_FILENAME} > /dev/null 2>&1
	fi
	SUM=`md5sum ${TESTFILES_PATH}/${TESTFILES_TARBALL_FILENAME}  | cut -d " " -f1`
	if [ "${SUM}" == "${TESTFILES_MD5SUM}" ]; then
	    break
	else
	    rm ${TESTFILES_TARBALL_FILENAME}
	fi
    done
    cd -
fi

if [ $REMOTE_MODE -eq 0 ]; then
    ping -c 1 ${REMOTE_HOST} > /dev/null 2>&1
    if [ "$?" -ne 0 ]; then
	echo "Failed to ping remote host '${REMOTE_HOST}'"
	exit
    fi
    echo "Uploading all required files to '${REMOTE_HOST}'..."
    rsync -avz $0 ${PEER_JARFILE} ${TESTFILES_PATH}/${TESTFILES_TARBALL_FILENAME} ${LOGNAME}@${REMOTE_HOST}:
    if [ "$?" -ne 0 ]; then
	echo "User '${LOGNAME}' failed to login to host '${REMOTE_HOST}'"
	exit
    fi
fi

# Create temporal dir to carry out this test

TEST_DIR=$(mktemp -d -t nanoP2P.XXXXXX) || exit 1
echo "Using tmpdir = ${TEST_DIR}"

# Untar test files into test directory
cd $TEST_DIR
cp ${TESTFILES_PATH}/${TESTFILES_TARBALL_FILENAME} .
echo "Extracting test files..."
tar zxvf ${TESTFILES_TARBALL_FILENAME} && rm ${TESTFILES_TARBALL_FILENAME}

# Recreate directories for all peers
PEERCOMMANDS_PREFIX=peer_commands
PEERDIR_PREFIX=peer

if [ $REMOTE_MODE -eq 0 ]; then
    # Files shared by peers 0 and 1, which will be runnig in host 1 (local box)
    for i in 0 1 ;
    do
	PEER_DIR=peer$i
	mkdir ${PEER_DIR}
	cp P.mp4 Q.pdf ${PEER_DIR}
    done
else
    # Peers 2, 3 and 4, which will be running in host 2 (remote)
    # Files shared by peer 2
    for i in 2 ;
    do
	PEER_DIR=peer$i
	mkdir ${PEER_DIR}
	cp P.mp4 Q.pdf N.jpg ${PEER_DIR}
    done
    # Files shared by peer 3 (none, but need to create folder)
    for i in 3 ;
    do
	PEER_DIR=${PEERDIR_PREFIX}$i
	mkdir ${PEER_DIR}
    done    
    # Files shared by peer 4
    for i in 4 ;
    do
	PEER_DIR=${PEERDIR_PREFIX}$i
	mkdir ${PEER_DIR}
	cp N.jpg ${PEER_DIR}  
    done    
fi

rm P.mp4 Q.pdf N.jpg

echo "Contents of shared folders created:"
find -mindepth 2 -type f | sort

# Copy jar files to temp dir
cp ${PEER_JARFILE} .

if [ $REMOTE_MODE -eq 0 ]; then
    ssh -Y -t $LOGNAME@$REMOTE_HOST "./${SCRIPT_BASENAME} ${PEER_JARFILE_BASENAME} ${TRACKER_IP} ${TEST_TYPE_STRING} remote" &
fi

NUM_PEERS=4
PEER_IP=`hostname --all-ip-addresses | cut -d " " -f1`

NAMED_PIPE_PREFIX=${TEST_DIR}/commands_pipe

if [ $TEST_TYPE -eq 1 ]; then
    TRACKER_OPTIONS=" -chunk 1024 -loss 0.2"
fi

if [ $REMOTE_MODE -eq 0 ]; then
    # Run tracker in local box (host1)
    xterm -geometry  180x24+0+0  -fa 'Monospace' -fs 10  -title "Tracker@${TRACKER_IP}" -e  "java -jar Tracker.jar ${TRACKER_OPTIONS}; read -p 'Press ENTER to close this window...' " &
    # Run Peers 0 and 1 in host 1, and create pipes to enter commands
    for PEER in 0 1 ; do
	# Create named pipe where peer will read commands from
	NAMED_PIPE=$NAMED_PIPE_PREFIX.$PEER
	if [[ ! -p $NAMED_PIPE ]]; then
	    mkfifo $NAMED_PIPE
	else
	    echo "Cannot create pipe"
	    exit
	fi
	# Compute xterm X-Y position on screen
	n=$(($PEER % 2))
	m=$(($PEER / 2))
	if [ $n -eq "0" ] ; then
	    XPOS=0
	else # Odd peers are shifted to the right (second column)
	    XPOS=1000
	fi
	# Shift down peers as we move on
	YPOS=`expr ${m} \* 150 \+ 420`
	xterm -geometry  100x8+${XPOS}+${YPOS}  -fa 'Monospace' -fs 10  -title "Peer${PEER}@${PEER_IP}" -e  "java -jar ${PEER_JARFILE} ${TRACKER_IP} ${TEST_DIR}/${PEERDIR_PREFIX}${PEER} < $NAMED_PIPE; read -p 'Press ENTER to close this window...' " &
    done
else
    for PEER in 2 3 4 ; do
	# Create named pipe where peer will read commands from
	NAMED_PIPE=$NAMED_PIPE_PREFIX.$PEER
	if [[ ! -p $NAMED_PIPE ]]; then
	    mkfifo $NAMED_PIPE
	else
	    echo "Cannot create pipe"
	    exit
	fi
	# Compute xterm X-Y position on screen
	n=$(($PEER % 2))
	m=$(($PEER / 2))
	if [ $n -eq "0" ] ; then
	    XPOS=0
	else # Odd peers are shifted to the right (second column)
	    XPOS=1000
	fi
	# Shift down peers as we move on
	YPOS=`expr ${m} \* 150 \+ 420`
	xterm -geometry  100x8+${XPOS}+${YPOS}  -fa 'Monospace' -fs 10  -title "Peer${PEER}@${PEER_IP}" -e  "java -jar ${PEER_JARFILE} ${TRACKER_IP} ${TEST_DIR}/${PEERDIR_PREFIX}${PEER} < $NAMED_PIPE; read -p 'Press ENTER to close this window...' " &
    done
fi

# Now send commands to each peer through corresponding named pipe
# Note: all subscripts are executed in parallel (background)

if [ $TEST_TYPE -eq 0 ]; then
    ###############################################################
    #    BASIC TEST                                               #
    ###############################################################
    if [ $REMOTE_MODE -eq 0 ]; then
	# Give the remote xterms time to start up...
	sleep 1
	
	####### PEER 0 #######
	( 
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.0 &

	####### PEER 1 #######
	( 
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.1 & 

    else  ##################### REMOTE HOST #####################

	####### PEER 2 #######
	(
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.2 &

	####### PEER 3 #######
	(
	    sleep 4
	    echo "query"
	    sleep 5
	    echo "download 0006" # Download Q.pdf
	    echo "query"
	    sleep 10
	    echo "download 5555" # Download N.jpg
	    sleep 10
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.3 &

	####### PEER 4 #######
	(
	    sleep 1
	    echo "list"
	    sleep 5
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.4 &

    fi
else
    ###############################################################
    #    ADVANCED TEST                                            #
    ###############################################################
    if [ $REMOTE_MODE -eq 0 ]; then
	# Give the remote xterms time to start up...
	sleep 1
	
	
	####### PEER 0 #######
	( 
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.0 &

	####### PEER 1 #######
	( 
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.1 & 

    else  ##################### REMOTE HOST #####################

	####### PEER 2 #######
	(
	    sleep 1
	    echo "list"
	    sleep 60
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.2 &

	####### PEER 3 #######
	(
	    sleep 4
	    echo "query"
	    sleep 5
	    echo "download 0006" # Download Q.pdf
	    sleep 10
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.3 &

	####### PEER 4 #######
	(
	    sleep 9
	    echo "query"
	    sleep 5
	    echo "download 0006" # Download Q.pdf
	    sleep 5
	    echo "download 0CC" # Download P.mp4
	    sleep 10
	    echo "quit"
	    # end of my commands
	) > $NAMED_PIPE_PREFIX.4 &

    fi
fi
