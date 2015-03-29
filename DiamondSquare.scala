import java.io._;
import Array._;
import util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.FileNotFoundException
import java.io.IOException

/**
	* Generate heightmap with diamond-square algorithm.
	* @author Jeremy ZYRA
	* @version 1.0
*/
object DiamondSquare {

	private var _size: Int = 0;
	private var _sizeX: Int = 0;
	private var _sizeY: Int = 0;
	private var _smooth: Double = 0;
	private var _img: BufferedImage = null;
	private var _matrice = ofDim[Int](_sizeX, _sizeY);
	private var _filename: String = "./a.bmp";

	/**
		* Init image's matrice.
	*/
	private def initImage() {
		var x: Int = 0;
		var y: Int = 0;
		for (x <- 0 to _sizeX-1) {
			for (y <- 0 to _sizeY-1) {
				_matrice(x)(y) = 0x00;
			}
		}
	}

	/**
		* Convert int to RGB color (0xFF to 0xFFFFFF for example)
		* @param int value between 0 and 255.
		* @return RGB representation.
	*/
	private def intToColor(color: Int): Int = {
		var result: Int = 0;
		var i: Int = 0;
		for (i <- 0 to 2) {
			result *= 0x100;
			result ^= color;
		}
		return result;
	}

	/**
		* Determine points's colors for square step.
		* @param x : x's position to middle point of the square.
		* @param y : y's position to middle point of the square.
		* @param step : width of the square.
	*/
	private def square(x: Int, y: Int, step: Int) {
		var demiStep: Int = step >> 1;
		var color: Int = 0;
		var value: Int = 0;
		color += _matrice(x-demiStep)(y-demiStep);
		color += _matrice(x+demiStep)(y+demiStep);
		color += _matrice(x+demiStep)(y-demiStep);
		color += _matrice(x-demiStep)(y+demiStep);
		color /= 4;
		if(_matrice(x)(y) == 0) {
			//Average + pseudo random value.
			value = (color + (Random.nextDouble() * (step * _smooth) * 2 - (step * _smooth))).toInt;
			if(value > 0xFF) {
				_matrice(x)(y) = 0xFF;
			} else if (value < 0) {
				_matrice(x)(y) = 0;
			} else {
				_matrice(x)(y) = value;
			}
		}
	}

	/**
		* Determine points's colors for diamond step.
		* @param x : x's position to middle point of the diamond.
		* @param y : y's position to middle point of the diamond.
		* @param step : width of the diamond.
	*/
	private def diamond(x: Int, y: Int, step: Int) {
		var demiStep: Int = step >> 1;
		var value: Int = 0;
		var color: Int = 0;
		var count: Int = 0;
		if(x+demiStep < _sizeX) {
			color += _matrice(x+demiStep)(y);
			count += 1;
		}
		if(x-demiStep > -1) {
			color += _matrice(x-demiStep)(y);
			count += 1;
		}
		if(y+demiStep < _sizeY) {
			color += _matrice(x)(y+demiStep);
			count += 1;
		}
		if(y-demiStep > -1) {
			color += _matrice(x)(y-demiStep);
			count += 1;
		}
		color /= count;
		if(_matrice(x)(y) == 0) {	
			//Average + pseudo random value.
			value = (color + (Random.nextDouble() * (step * _smooth) * 2 - (step * _smooth))).toInt;
			if(value > 0xFF) {
				_matrice(x)(y) = 0xFF;
			} else if (value < 0) {
				_matrice(x)(y) = 0x00;
			} else {
				_matrice(x)(y) = value;
			}
		}
	}

	/**
		* Run diamond-square algorithm.
	*/
	private def diamondSquare() {
		var x: Int = 0;
		var y: Int = 0;
		var step: Int = _size-1;
		var demiStep: Int = _size;
		var size: Int = _size;
		//While width is not equal to 1.
		while(step != 1) {
			demiStep = step >> 1;
			//Foreach square.
			for (x <- 0 to _sizeX-1 by step) {
				for (y <- 0 to _sizeX-1 by step) {
					if(x+demiStep < _sizeX && y+demiStep < _sizeY) {
						square(x+demiStep, y+demiStep, step);
					}
				}
			}
			//Foreach diamond.
			for (x <- 0 to _sizeX-1 by step) {
				for (y <- (x+demiStep)%step to _sizeY-1-demiStep by step) {
					if(x+demiStep < _sizeX && y+demiStep < _sizeY) {
						diamond(x, y, step);
						diamond(x+demiStep, y-demiStep, step);
						diamond(x+step, y, step);
						diamond(x+demiStep, y-demiStep+step, step);
					}
				}
			}
			step >>= 1;
		}
	}

	/**
		* Save image in bmp format.
	*/
	private def saveImage() {
		for (x <- 0 to _sizeX-1) {
			for (y <- 0 to _sizeY-1) {
				_img.setRGB(x, y, intToColor(_matrice(x)(y)));
			}
		}
		try {
			var out:FileOutputStream = new FileOutputStream(_filename);
			ImageIO.write(_img, "bmp", out);
			out.close();
		} catch {
			case e: java.io.FileNotFoundException => println("[-] Incorrect path: " + _filename);
		}
	}

	/**
		* Print usage.
	*/
	private def printHelp() {
		println("NAME");
		println("\tDiamondSquare.jar\n");
		println("SYNOPSIS");
		println("\tDiamondSquare.jar [-out FILENAME] -smooth COEFFICIENT -size SIZE\n");
		println("DESCRIPTION");
		println("\tGenerate heightmap with diamond-square algorithm.");
		println("\tImage generated is in bmp format.\n");
		println("\t-output");
		println("\t\tPath to the image output. (./a.bmp by default)\n");
		println("\t-smooth");
		println("\t\tSmooth coefficient. (between 0 and 1)\n");
		println("\t-size");
		println("\t\tImage's size in px. (must be 2^n+1)\n");
		println("AUTHOR");
		println("\tJeremy ZYRA");
		sys.exit;
	}

	/**
		* Manage program's arguments.
		* @param String array of arguments.
	*/
	private def getOpts(args: Array[String]) {
		var i: Int = 0;
		var smooth: Double = 0;
		var size: Int = 0;
		var out: String = "";
		for (i <- 0 to args.length-2) {
			args(i) match {
				case "-smooth" => smooth = (args(i+1)).toDouble;
				case "-size" => size = (args(i+1)).toInt;
				case "-out" => _filename = args(i+1);
				case whoa =>;
			}
		}
		if ((smooth == 0 && size == 0 && out == "")) {
			printHelp();
		}
		if (!(smooth > 0 && smooth < 1)) {
			println("[-] Error: Smooth must be between 0 and 1.");
			sys.exit;
		}
		if (!(((size-1) & ((size-2))) == 0)) {
			println("[-] Error: Size must be : 2^n+1.");
			sys.exit;
		}
		_size = size;
		_sizeX = _size;
		_sizeY = _size;
		_smooth = smooth;
		_img = new BufferedImage(_sizeX, _sizeY, 1);
		_matrice = ofDim[Int](_sizeX, _sizeY);
	}

	/**
		* Entry point program.
		* @param String array of arguments.
	*/
	def main(args: Array[String]) {
		getOpts(args);
		initImage();
		diamondSquare();
		saveImage();
	}

}
