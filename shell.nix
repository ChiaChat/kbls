{ pkgs ? import <nixpkgs> { } }:
with pkgs;
mkShell {
  buildInputs = [
    jetbrains.idea-ultimate
    jetbrains.webstorm
    jdk
    gradle
    yarn
    nodePackages.npm
    nodejs
  ];

  shellHook = ''
    idea-ultimate . &  webstorm bls-signatures/ && fg
  '';
}
