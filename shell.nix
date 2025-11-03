let
  pkgs = import <nixpkgs> { config = { allowUnfree = true; }; };
in
pkgs.mkShell {
  buildInputs = [
    pkgs.jdk21
    pkgs.gradle
    pkgs.awscli2
    pkgs.terraform
    pkgs.git
    pkgs.openssl
    pkgs.gh
    pkgs.act
    pkgs.codex
    pkgs.oauth2l
  ];

  LANG = "en_US.UTF-8";
  LC_ALL = "en_US.UTF-8";

  shellHook = ''
    export E_API_HOME=$(pwd)

    if [ -e .env ]; then
      source .env
    else
      echo "Warning: no .env file" 1>&2
    fi

    export JAVA_HOME=${pkgs.jdk21}
    export PATH=$JAVA_HOME/bin:$PATH

    export CODEX_HOME=${pkgs.codex}
    export PATH=$CODEX_HOME/bin:$PATH

    export LANG=en_US.UTF-8
  '';
}

