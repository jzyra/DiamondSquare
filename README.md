# DiamondSquare
Generate heightmap image with diamond-square algorithm.

#Usage

For use this tool : 

    	DiamondSquare.jar [-out FILENAME] -smooth COEFFICIENT -size SIZE
    	-out : Path to heightmap (generated in bmp format).
    	-smooth : Coefficient between 0 and 1.
    	-size : Image size (width and height in pixels), size must be 2^n+1. (2049 for example)

You can view 3D rendering with this tool : https://github.com/jzyra/HeightmapViewer

#Build

For build this tool, you must install scala and run : 

    scalac DiamondSquare.scala
    scala DiamondSquare

#Demonstration

It's heightmap monochrome :

![Heightmap](https://raw.githubusercontent.com/jzyra/DiamondSquare/master/example.bmp)
