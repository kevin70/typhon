#!/bin/sh


# -----------------------------------------------------------------------------
# Control Script for the TYPHON Server
#
# Environment Variable Prerequisites
#
#   Do not set the variables in this script. Instead put them into a script
#   setenv.sh in TYPHON_HOME/bin to keep your customizations separate.
#
#   TYPHON_HOME   May point at your Catalina "build" directory.
#
#   TYPHON_OUT      (Optional) Full path to a file where stdout and stderr
#                   will be redirected.
#                   Default is $TYPHON_HOME/logs/typhon.out
#
#   TYPHON_OPTS     (Optional) Java runtime options used when the "start",
#                   "run" or "debug" command is executed.
#                   Include here and not in JAVA_OPTS all options, that should
#                   only be used by Tomcat itself, not by the stop process,
#                   the version command etc.
#                   Examples are heap size, GC logging, JMX ports etc.
#
#   TYPHON_TMPDIR   (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $TYPHON_HOME/temp.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" argument.
#
#   JRE_HOME        Must point at your Java Runtime installation.
#                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
#                   are both set, JRE_HOME is used.
#
#   JAVA_OPTS       (Optional) Java runtime options used when any command
#                   is executed.
#                   Include here and not in TYPHON_OPTS all options, that
#                   should be used by Tomcat and also by the stop process,
#                   the version command etc.
#                   Most options should go into TYPHON_OPTS.
#
#   JAVA_ENDORSED_DIRS (Optional) Lists of of colon separated directories
#                   containing some jars in order to allow replacement of APIs
#                   created outside of the JCP (i.e. DOM and SAX from W3C).
#                   It can also be used to update the XML parser implementation.
#                   Defaults to $TYPHON_HOME/endorsed.
#
#   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
#                   command is executed. The default is "dt_socket".
#
#   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. The default is 8000.
#
#   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. Specifies whether JVM should suspend
#                   execution immediately after startup. Default is "y".
#
#   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
#                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
#                   and JPDA_SUSPEND are ignored. Thus, all required jpda
#                   options MUST be specified. The default is:
#
#                   -agentlib:jdwp=transport=$JPDA_TRANSPORT,
#                       address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
#
#   TYPHON_PID      (Optional) Path of the file which should contains the pid
#                   of the catalina startup java process, when start (fork) is
#                   used
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set TYPHON_HOME if not already set
[ -z "$TYPHON_HOME" ] && TYPHON_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [ -r "$TYPHON_HOME/bin/setenv.sh" ]; then
  . "$TYPHON_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$TYPHON_HOME" ] && TYPHON_HOME=`cygpath --unix "$TYPHON_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND
    
  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  . "$TYPHON_HOME"/bin/setclasspath.sh
else
  if [ -r "$TYPHON_HOME"/bin/setclasspath.sh ]; then
    . "$TYPHON_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $TYPHON_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
      exit 1
  fi
fi

# Add on extra jar files to CLASSPATH
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi
CLASSPATH="$CLASSPATH""$TYPHON_HOME"/bin/bootstrap.jar

if [ -z "$TYPHON_OUT" ] ; then
  TYPHON_OUT="$TYPHON_HOME"/logs/typhon.out
fi

if [ -z "$TYPHON_TMPDIR" ] ; then
  TYPHON_TMPDIR="$TYPHON_HOME"/temp
fi

if [ ! -d "$TYPHON_HOME"/logs ]; then
  mkdir -p "$TYPHON_HOME"/logs
fi

if [ ! -d "$TYPHON_HOME"/temp ]; then
  mkdir -p "$TYPHON_HOME"/temp
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
  have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  TYPHON_HOME=`cygpath --absolute --windows "$TYPHON_HOME"`
  TYPHON_TMPDIR=`cygpath --absolute --windows "$TYPHON_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

if [ "$1" = "jpda" ] ; then
  if [ -z "$JPDA_TRANSPORT" ]; then
    JPDA_TRANSPORT="dt_socket"
  fi
  if [ -z "$JPDA_ADDRESS" ]; then
    JPDA_ADDRESS="8000"
  fi
  if [ -z "$JPDA_SUSPEND" ]; then
    JPDA_SUSPEND="y"
  fi
  if [ -z "$JPDA_OPTS" ]; then
    JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
  fi
  TYPHON_OPTS="$TYPHON_OPTS $JPDA_OPTS"
  echo "Listening for transport dt_socket at address: $JPDA_ADDRESS"
  shift
fi

#
if [ "$1" = "start" ] ; then

  if [ ! -z "$TYPHON_PID" ]; then
    if [ -f "$TYPHON_PID" ]; then
      if [ -s "$TYPHON_PID" ]; then
      echo "Existing PID file found during start."
        if [ -r "$TYPHON_PID" ]; then
          PID=`cat "$TYPHON_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
          echo "Typhon appears to still be running with PID $PID. Start aborted."
            exit 1
          else
          echo "Removing/clearing stale PID file."
            rm -f "$TYPHON_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$TYPHON_PID" ]; then
                cat /dev/null > "$TYPHON_PID"
              else
              echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
        echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$TYPHON_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$TYPHON_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift
  touch "$TYPHON_OUT"
  
  eval \"$_RUNJAVA\" $JAVA_OPTS $TYPHON_OPTS \
    -Djava.endorsed.dirs=\"$JAVA_ENDORSED_DIRS\" \
    -Djava.io.tmpdir=\"$TYPHON_TMPDIR\" \
    -classpath \"$CLASSPATH\" \
    -Dtyphon.home=\"$TYPHON_HOME\" \
    org.skfiy.typhon.startup.Bootstrap "$@" start \
    >> "$TYPHON_OUT" 2>&1 "&"

  if [ ! -z "$TYPHON_PID" ]; then
    echo $! > "$TYPHON_PID"
  fi

elif [ "$1" = "stop" ] ; then

  shift

  SLEEP=5
  if [ ! -z "$1" ]; then
  echo $1 | grep "[^0-9]" >/dev/null 2>&1
    if [ $? -gt 0 ]; then
      SLEEP=$1
      shift
    fi
  fi

  FORCE=0
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  if [ ! -z "$TYPHON_PID" ]; then
    if [ -f "$TYPHON_PID" ]; then
      if [ -s "$TYPHON_PID" ]; then
        kill -0 `cat "$TYPHON_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
        echo "PID file found but no matching process was found. Stop aborted."
          exit 1
        fi
      else
      echo "PID file is empty and has been ignored."
      fi
    else
    echo "\$TYPHON_PID was set but the specified file does not exist. Is Tomcat running? Stop aborted."
      exit 1
    fi
  fi

  eval \"$_RUNJAVA\" $JAVA_OPTS \
    -Djava.endorsed.dirs=\"$JAVA_ENDORSED_DIRS\" \
    -Djava.io.tmpdir=\"$TYPHON_TMPDIR\" \
    -classpath \"$CLASSPATH\" \
    -Dtyphon.home=\"$TYPHON_HOME\" \
    org.skfiy.typhon.startup.Bootstrap "$@" stop

  if [ ! -z "$TYPHON_PID" ]; then
    if [ -f "$TYPHON_PID" ]; then
      while [ $SLEEP -ge 0 ]; do
        kill -0 `cat "$TYPHON_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
          rm -f "$TYPHON_PID" >/dev/null 2>&1
          if [ $? != 0 ]; then
            if [ -w "$TYPHON_PID" ]; then
              cat /dev/null > "$TYPHON_PID"
            else
            echo "Typhon stopped but the PID file could not be removed or cleared."
            fi
          fi
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          if [ $FORCE -eq 0 ]; then
          echo "Tomcat did not stop in time. PID file was not removed."
          fi
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
    fi
  fi

  KILL_SLEEP_INTERVAL=5
  if [ $FORCE -eq 1 ]; then
    if [ -z "$TYPHON_PID" ]; then
    echo "Kill failed: \$TYPHON_PID not set"
    else
      if [ -f "$TYPHON_PID" ]; then
        PID=`cat "$TYPHON_PID"`
      echo "Killing Tomcat with the PID: $PID"
        kill -9 $PID
        while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
            kill -0 `cat "$TYPHON_PID"` >/dev/null 2>&1
            if [ $? -gt 0 ]; then
                rm -f "$TYPHON_PID" >/dev/null 2>&1
                if [ $? != 0 ]; then
                  echo "Tomcat was killed but the PID file could not be removed."
                fi
                break
            fi
            if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
                sleep 1
            fi
            KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
        done
        if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
          echo "Tomcat has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
        fi
      fi
    fi
  fi

elif [ "$1" = "version" ] ; then

    "$_RUNJAVA"   \
      -classpath "$TYPHON_HOME/bin/bootstrap.jar" \
      org.skfiy.typhon.startup.Bootstrap version

else

  echo "Usage: typhon.sh ( commands ... )"
  echo "commands:"
  echo "  jpda start        Start Typhon under JPDA debugger"
  echo "  start             Start Typhon in a separate window"
  echo "  stop              Stop Typhon, waiting up to 5 seconds for the process to end"
  echo "  version           What version of tomcat are you running?"
  echo "Note: Waiting for the process to end and use of the -force option require that \$TYPHON_PID is defined"
  exit 1

fi