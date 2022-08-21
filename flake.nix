{
  description = "A very basic flake";

  inputs = {
    nixpkgs.url = "github:abueide/nixpkgs/master";
  };


  outputs = { self, nixpkgs, flake-utils }: 
  flake-utils.lib.eachDefaultSystem(
    system:
    let pkgs = nixpkgs.legacyPackages.${system}; in {
      devShells.default = import ./shell.nix { inherit pkgs; };
    }
    );
  }
