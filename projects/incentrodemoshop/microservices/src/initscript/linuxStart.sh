#!/bin/bash
#
# chkconfig: 345 80 20
# description: Start and stop script for gradle created java application startup
#
# This is a generic file that can be used by any distribution from gradle ("gradle distZip").
# Link this file to the name of the process you want to run.
# e.g.
#   ln -s /path/to/gradle-init-start-stop /etc/init.d/ivy-jetty
#
# it requires a conf file /etc/NAME.conf, e.g. /etc/ivy-jetty.conf
# otherwise it will quit.
#

BASENAME=$(basename $0)
maxShutdownTime=15

USERNAME=<@osuser@>
PROG=<@execPath@>/@execName@
SERVICENAME=<@serviceName@>
JAVACLASS=@mainclassname@

JAVA_HOME=<@javaHome@>

pidfile=/var/run/$BASENAME.pid

checkProcessIsRunning() {
  local pid="$1"
  if [ -z "$pid" -o "$pid" == " " ]; then return 1; fi
  if [ ! -e /proc/$pid ]; then return 1; fi
  return 0
}

checkProcessIsOurService() {
  local pid="$1"
  if [ "$(ps -p $pid --no-headers -o comm)" != "java" ]; then return 1; fi
  grep -q --binary -F "$JAVACLASS" /proc/$pid/cmdline
  if [ $? -ne 0 ]; then return 1; fi
  return 0
}

getServicePID() {
  if [ ! -f $pidfile ]; then return 1; fi
  pid="$(<$pidfile)"
  checkProcessIsRunning $pid || return 1
  checkProcessIsOurService $pid || return 1
  return 0
}

startService() {
  cmd="export JAVA_HOME=$JAVA_HOME; nohup $PROG >/dev/null 2>&1 & echo \$!"
  su - $USERNAME -c "$cmd" > $pidfile
  sleep 0.2
  pid="$(<$pidfile)"
  if checkProcessIsRunning $pid; then
    return 0
  else
    return 1
  fi
}

start() {
  getServicePID
  if [ $? -eq 0 ]; then echo -n "$SERVICENAME is already running"; RETVAL=0; echo ""; return 0; fi

  echo -n "Starting $SERVICENAME: "
  startService
  if [ $? -ne 0 ] ; then
    echo "failed"
    return 1
  else
    echo "started"
    return 0
  fi
}

stopService() {
  # soft kill first...
  kill $pid || return 1

  # check if process dead, sleep 0.2s otherwise
  for ((i=0; i<maxShutdownTime*5; i++)); do
    checkProcessIsRunning $pid
    if [ $? -ne 0 ] ; then
      rm -f $pidfile
      return 0
    fi
    sleep 0.2
  done

  # hard kill now...
  kill -s KILL $pid || return 1

  # check if process dead, sleep 0.2s otherwise
  for ((i=0; i<maxShutdownTime*5; i++)); do
    checkProcessIsRunning $pid
    if [ $? -ne 0 ] ; then
      rm -f $pidfile
      return 0
    fi
    sleep 0.2
  done
  return 1
}

stop() {
  getServicePID
  if [ $? -ne 0 ]; then echo -n "$SERVICENAME is not running"; RETVAL=0; echo ""; return 0; fi
  pid="$(<$pidfile)"

  echo -n "Stopping $SERVICENAME "
  stopService
  if [ $? -ne 0 ]; then RETVAL=1; echo "failed"; return 1; fi
  echo "stopped PID=$pid"
  RETVAL=0
  return 0
}

restart() {
  stop
  start
}

checkServiceStatus() {
  echo -n "Checking for $SERVICENAME:   "
  if getServicePID; then
    echo "running PID=$pid"
    RETVAL=0
  else
    echo "stopped"
    RETVAL=3
  fi
  return 0;
}


####### START OF MAIN SCRIPT

RETVAL=0
case "$1" in
  start)
    $1
    ;;
  stop)
    $1
    ;;
  restart)
    $1
    ;;
  status)
    checkServiceStatus
    ;;
  *)
    echo "Usage: $0 {start|stop|status|restart}"
    exit 1
esac
exit $RETVAL
