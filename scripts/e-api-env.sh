#
# Intended to be sourced from your .bashrc or .profile
# Do something like this:
#
# E_API_HOME=$HOME/java-api
# if [ -d $E_API_HOME ]; then
#   source $E_API_HOME/scripts/e-api-env.sh
# fi
#

export E_API_HOME=$HOME/java-api

e_api_up() {
  (
    cd $E_API_HOME
    if [ ! -e $E_API_HOME/.env ]; then
      if [ -e $HOME/.e-api-env ]; then
        ln -s $HOME/.e-api-env $E_API_HOME/.env
      fi
    fi

    source "$E_API_HOME/.env"
    nix develop
  )
}
alias "e-api-up"=e_api_up

e_api_cursor() {
  (
    if [ -z "$E_API_HOME" ]; then
      echo "E_API_HOME not defined"
      exit 1
    fi

    cd $E_API_HOME
    nix develop --command steam-run cursor $E_API_HOME
  )
}
alias "e-api-cursor"=e_api_cursor
alias "cd-"="cd $E_API_HOME"


