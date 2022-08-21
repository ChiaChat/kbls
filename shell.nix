{ pkgs ? import <nixpkgs> { } }:
with pkgs;
mkShell {
  buildInputs = [
    jetbrains.idea-ultimate
    jdk
    gradle
  ];

  shellHook = ''
    idea-ultimate .
  '';
}
