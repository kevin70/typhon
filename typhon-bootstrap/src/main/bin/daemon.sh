#!/bin/sh
# Copyright (C) 2012-2013 The Skfiy Open Association.

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#-----------------------------------------------------------------------------
# Commons Daemon wrapper script.
#-----------------------------------------------------------------------------
#
# resolve links - $0 may be a softlink
ARG0="$0"
while [ -h "$ARG0" ]; do
  ls=`ls -ld "$ARG0"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    ARG0="$link"
  else
    ARG0="`dirname $ARG0`/$link"
  fi
done

DIRNAME="`dirname $ARG0`"
PROGRAM="`basename $ARG0`"

while [ ".$1" != .]
do
  case "$1" in
    --java-home )
      JAVA_HOME="$2"
      shift; shift;
      continue;
    ;;
    --typhon-home )
      TYPHON_HOME="$2"
      shift; shift;
      continue
    ;;
    --typhon-pid )
      TYPHON_PID="$2"
      shift; shift;
      continue
    ;;
    --typhon-user )
      TYPHON_USER="$2"
      shift; shift;
      continue
    ;;
    * )
      break;
    ;;
  esac
done

# OS specific support (must be 'true' or 'false').
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*)
    cygwin=true
  ;;
  Darwin*)
    darwin=true
  ;;
esac

# Use the maximum available, or set MAX_FD != -1 to use that
test ".$MAX_FD" = . && MAX_FD="maximum"
# Setup parameters for running the jsvc
#
test ".$TYPHON_USER" = . && TYPHON_USER=typhon
# Set JAVA_HOME to working JDK or JRE
# JAVA_HOME=/usr/jdk-1.6.0.22
# If not set we'll try to guess the JAVA_HOME
# from java binary if on the PATH
#
if [ -z "$JAVA_HOME" ]; then
  JAVA_BIN="`which java 2>/dev/null || type java 2>&1`"
  test -x "$JAVA_BIN" && JAVA_HOME="`dirname $JAVA_BIN`"
  test ".$JAVA_HOME" != . && JAVA_HOME=`cd "$JAVA_HOME/.." >/dev/null; pwd`
else
  JAVA_BIN="$JAVA_HOME/bin/java"
fi

# Only set TYPHON_HOME if not already set
test ".$TYPHON_HOME" = . && TYPHON_HOME=`cd "$DIRNAME/.." >/dev/null; pwd`
test ".$TYPHON_MAIN" = . && TYPHON_MAIN=org.skfiy.typhon.startup.Bootstrap
test ".$JSVC" = . && JSVC="$TYPHON_HOME/bin/jsvc"


# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=
JAVA_OPTS=
if [ -r "$TYPHON_HOME/bin/setenv.sh" ]; then
  . "$TYPHON_HOME/bin/setenv.sh"
fi

# Add on extra jar files to CLASSPATH
test ".$CLASSPATH" != . && CLASSPATH="$CLASSPATH:"
CLASSPATH="$CLASSPATH$TYPHON_HOME/bin/commons-daemon.jar:$TYPHON_HOME/bin/bootstrap.jar"

test ".$TYPHON_OUT" = . && TYPHON_OUT="$TYPHON_HOME/logs/typhon-daemon.out"
test ".$TYPHON_TMPDIR" = . && TYPHON_TMPDIR="$TYPHON_HOME/temp"

# Don't override the endorsed dir if the user has set it previously
if [ -z "$JAVA_ENDORSED_DIRS" ]; then
  # Set the default -Djava.endorsed.dirs argument
  JAVA_ENDORSED_DIRS="$TYPHON_HOME"/endorsed
fi

# Set -pidfile
test ".$TYPHON_PID" = . && TYPHON_PID="$TYPHON_HOME/bin/typhon-daemon.pid"

# Increase the maximum file descriptors if we can
if [ "$cygwin" = "false" ]; then
  MAX_FD_LIMIT=`ulimit -H -n`
  if [ "$?" -eq 0 ]; then
    # Darwin does not allow RLIMIT_INFINITY on file soft limit
    if [ "$darwin" = "true" -a "$MAX_FD_LIMIT" = "unlimited" ]; then
      MAX_FD_LIMIT=`/usr/sbin/sysctl -n kern.maxfilesperproc`
    fi
    test ".$MAX_FD" = ".maximum" && MAX_FD="$MAX_FD_LIMIT"
    ulimit -n $MAX_FD
    if [ "$?" -ne 0 ]; then
      echo "$PROGRAM: Could not set maximum file descriptor limit: $MAX_FD"
    fi
  else
    echo "$PROGRAM: Could not query system maximum file descriptor limit: $MAX_FD_LIMIT"
  fi
fi

# ----- Execute The Requested Command -----------------------------------------
case "$1" in
  start)
    "$JSVC" $JSVC_OPTS \
    -java-home "$JAVA_HOME" \
    -user "$TYPHON_USER" \
    -pidfile "$TYPHON_PID" \
    -wait 10 \
    -outfile "$TYPHON_OUT" \
    -errfile "&1" \
    -classpath "$CLASSPATH" \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" \
    -Dtyphon.home="$TYPHON_HOME" \
    -Djava.io.tmpdir="$TYPHON_TMPDIR" \
    $TYPHON_MAIN
    exit $?
  ;;
  stop)
    "$JSVC" $JSVC_OPTS \
    -stop \
    -pidfile "$TYPHON_PID" \
    -classpath "$CLASSPATH" \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" \
    -Dtyphon.home="$TYPHON_HOME" \
    -Djava.io.tmpdir="$TYPHON_TMPDIR" \
    $TYPHON_MAIN
    exit $?
  ;;
  *)
    echo "Unkown command: '$1'"
    echo "Usage: $PROGRAM ( commands ... )"
    echo "commands:"
    echo "  start             Start Typhon"
    echo "  stop              Stop Typhon"
    echo "  version           What version of commons daemon and Typhon"
    echo "                    are you running?"
    exit 1
  ;;
esac
