// COMMANDOS BEL TOOL
/*
    INPUT:   BEL *.SEC file (as argument or in prompt)
    OUTPUT:  C2 *.SEC file  (filename+".SEC")
    USAGE:   java SEC5 <file.SEC> [scale isSnow [offset_x offset_y]]
				scale:    float (default:1.0)
				isSnow:   int   (default:1, 0=false, 1=true)
				offset_x: int   (default:0)
				offset_y: int   (default:0)
*/

import java.io.*;
import java.util.*;

public class SEC5 {

  public static void main (String [] args) {

    try {
	  
	  float scale = 1.0f;
	  boolean isSnow = true; // if false: terrain is sand
	  int offset_x = 0;
      int offset_y = 0;
	
	  String filename;
	  if (args.length==0) {
	    System.out.print("\n\n>");
	    Scanner scan = new Scanner(System.in);
	    filename = scan.next();
	  } else {
	    filename = args[0];
		if (args.length>=3) {
		  scale = Float.parseFloat(args[1]);
		  if (Integer.parseInt(args[2])==0) {
		    isSnow = false;
		  }
		}
		if (args.length==5) {
          offset_x = Integer.parseInt(args[3]);
          offset_y = (int)(Integer.parseInt(args[4])*1.5557);
        }
	  }
	  
	  File file = new File(filename);
	  if (!file.exists()) {
	    System.out.println("\nFile "+filename+" doesn't exist!");
		System.exit(1);
	  }
	  
	  String filename2 = file.getAbsolutePath();
	
	  byte header[] = {1,0,0,0,1,0,0,0,0,0,0,0,77,65,80,49,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	  String str;
	  int i, j, k;
	
      DataInputStream dis = new DataInputStream(new FileInputStream(filename2));
	  Scanner sc = new Scanner(dis);
	  
	  int nbrOfPoints = sc.nextInt();
	  sc.nextLine();
	  float point_x[]  = new float[nbrOfPoints];
	  float point_y[]  = new float[nbrOfPoints];
	  int point1[]     = new int[nbrOfPoints*8];
	  int point2[]     = new int[nbrOfPoints*8];
	  int sector1[]    = new int[nbrOfPoints*8];
	  int sector2[]    = new int[nbrOfPoints*8];
	  int nbrOfBorders = 0;

	  float MINx = java.lang.Float.MAX_VALUE;
	  float MINy = java.lang.Float.MAX_VALUE;
	  float MAXx = java.lang.Float.MIN_VALUE;
	  float MAXy = java.lang.Float.MIN_VALUE;
	  
	  // read points
	  for (i=0;i<nbrOfPoints;i++) {
	    str = sc.next();
		point_x[i] = (new Float(str) * scale) + offset_x;
		if (point_x[i] < MINx) MINx = point_x[i];
		else if (point_x[i] > MAXx) MAXx = point_x[i];
		str = sc.next();
		
		point_y[i] = (new Float(str) * -1 * scale) - offset_y; // mirror y
	    if (point_y[i] < MINy) MINy = point_y[i];
		else if (point_y[i] > MAXy) MAXy = point_y[i];
		sc.nextLine();
	  }
	  
	  int nbrOfMeshes_x = (int) java.lang.Math.ceil((MAXx - MINx) / 64);
	  int nbrOfMeshes_y = (int) java.lang.Math.ceil((MAXy - MINy) / 64);
	  
	  int grid[][] = new int[nbrOfMeshes_x*nbrOfMeshes_y][64];
	  int nbrOfDistricts = 0;
	  
	  sc.nextLine();
	  int nbrOfSectors = sc.nextInt();
	  sc.nextLine();
	  
	  int nbrOfVertices[] = new int[nbrOfSectors];
	  float kx[]          = new float[nbrOfSectors];
	  float ky[]          = new float[nbrOfSectors];
	  float bz[]          = new float[nbrOfSectors];
	  int type[]          = new int[nbrOfSectors];
	  float height[]      = new float[nbrOfSectors];
	  float offset[]      = new float[nbrOfSectors];
	  int flags[]         = new int[nbrOfSectors];
	  int points[][]      = new int[nbrOfSectors][32];
	  
	  // read sectors
	  boolean isSpecialSector[] = new boolean[nbrOfSectors];
	  
	  for (i=0;i<nbrOfSectors;i++) {

	    isSpecialSector[i] = false;
	  
	    nbrOfVertices[i]  = sc.nextInt();
		str               = sc.next();
		kx[i]             = new Float(str);
		str               = sc.next();
		ky[i]             = new Float(str) * -1; // mirror y
		str               = sc.next();
		bz[i]             = new Float(str) * scale;
		/*
		if (kx[i] == 0 && ky[i] == 0) {
		  bz[i] *= scale;
		} else {
		  bz[i] = bz[i] * scale - bz[i];
		}
		*/
		type[i]           = sc.nextInt();
		str               = sc.next();
		height[i]         = new Float(str) * scale;
		str               = sc.next();
	    offset[i]         = new Float(str) * scale;
		flags[i]          = sc.nextInt();
		sc.nextLine();
		// read vertices
		for (j=0;j<nbrOfVertices[i];j++) {
		  points[i][j] = sc.nextInt();
		  sc.nextLine();
		}
		sc.nextLine();

		// set borders
		for (j=0;j<nbrOfVertices[i]-1;j++) {
		  point1[nbrOfBorders]  = points[i][j];
		  point2[nbrOfBorders]  = points[i][j+1];
		  sector1[nbrOfBorders] = i;
		  sector2[nbrOfBorders] = -1; // FF FF FF FF
		  for (k=0;k<nbrOfBorders;k++) {
		    if (point1[k] == point2[nbrOfBorders]) {
			  if (point2[k] == point1[nbrOfBorders]) {
			    sector2[k] = i;
				sector2[nbrOfBorders] = sector1[k];
			  }
			}
		  }
		  nbrOfBorders++;
		}
		
		point1[nbrOfBorders]  = points[i][j];
		point2[nbrOfBorders]  = points[i][0];
		sector1[nbrOfBorders] = i;
		sector2[nbrOfBorders] = -1; // FF FF FF FF
		for (k=0;k<nbrOfBorders;k++) {
		  if (point1[k] == point2[nbrOfBorders]) {
			if (point2[k] == point1[nbrOfBorders]) {
			  sector2[k] = i;
			  sector2[nbrOfBorders] = sector1[k];
		    }
	      }
		}
		nbrOfBorders++;
	  }
	  
      dis.close();
	  
	  DataOutputStream fos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename2+".SEC")));
	  // writing/reading to/from "temp_sec" is used to do a little<>big endian conversion
      DataOutputStream tmp = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("temp_sec")));

	  // get number of special sectors
	  int nbrOfSpecialSectors = 0;
	  for (i=0;i<nbrOfBorders;i++) {
	    if (sector2[i] == -1) {
		  isSpecialSector[sector1[i]] = true;
		}
	  }
	  for (i=0;i<nbrOfSectors;i++) {
	    if (isSpecialSector[i]) nbrOfSpecialSectors++;
	  }
	  
	  // write header
	  fos.write(header);
	  int  nbr[]  = {nbrOfPoints,nbrOfBorders,nbrOfSectors,nbrOfSpecialSectors};
	  for (i=0;i<4;i++) {
	    byte temp[] = {0,0,0,0};
		temp[0] = (byte) (nbr[i] % 256);
		temp[1] = (byte) (nbr[i] / 256);
		fos.write(temp);
	  }
	  
	  // write points
	  for (i=0;i<nbrOfPoints;i++) {
	    tmp.writeFloat(point_x[i]);
		tmp.writeFloat(point_y[i]);
	  }
	  tmp.flush();
	  tmp.close();
	  
	  FileInputStream in = new FileInputStream("temp_sec");
	  byte[] mem = new byte[nbrOfPoints*8];
	  in.read(mem);
	  in.close();
	  
	  j=0;
	  for (i=0;i<nbrOfPoints*2;i++) {
	    fos.writeByte(mem[j+3]);
		fos.writeByte(mem[j+2]);
		fos.writeByte(mem[j+1]);
		fos.writeByte(mem[j]);
		j+=4;
	  }
	  
	  // write borders
	  i = 0;
	  for (int ii=0;ii<nbrOfSectors;ii++) {
		for (k=0;k<nbrOfVertices[ii];k++) {
		  int  brd[]  = {point2[i+k],point1[i+k],sector1[i+k],sector2[i+k],0};
		  for (j=0;j<5;j++) {
	        if (j==3 && sector2[i+k]==-1) {
		      byte temp[] = {-1,-1,-1,-1};
			  fos.write(temp);
		    } else {
		      byte temp[] = new byte[4];
		      temp[0] = (byte) (brd[j] % 256);
		      temp[1] = (byte) (brd[j] / 256);
			  temp[2] = 0;
			  temp[3] = 0;
			  fos.write(temp);
		    }
		  }
		}
		i+=nbrOfVertices[ii];
	  }

	  // write sectors
	  int border_count = 0;
	  for (i=0;i<nbrOfSectors;i++) {
	  
	    // number of borders
	    fos.write((byte) nbrOfVertices[i]);
		fos.write(0);
		fos.write(0);
		fos.write(0);
		
		// terrain info
		boolean isDiggable   = false;
		boolean useFootprint = false;
		boolean isEnterable  = true;
        boolean isInvisible  = false;
		boolean isStair      = false;
		boolean isPlane      = true;
		boolean isBridge     = false;
		
		switch (type[i]) {
          case 0: { fos.write(0); fos.write(0);  break; } //  LAND
          case 1: { fos.write(3); fos.write(13); break; } //  SHALLOW WATER
		  case 2: {
			if (isSnow) { 	// SNOW
			  fos.write(1);
			  fos.write(7);
			} else { 		// SAND
			  fos.write(0);
			  fos.write(0);
			}
			isDiggable   = true;
			useFootprint = true;
			break;
		  }
          case 3: { fos.write(2); fos.write(14); break; } //  DEEP WATER WATER
        }
		fos.write(0);
		fos.write(0);
		
		int x = flags[i] % 32;
        if (1 == x/16) isInvisible = true;
        x = x % 8;
        if (1 == x/4)  isEnterable = false;
		x = x % 4; 
		if (1 == x/2)  isBridge = true;
        x = x % 2;
        if (1 == x) { 
          //isStair = true;
		  isPlane = false;
        }
		
		if (isPlane) x=4; else x=0;
		if (isEnterable)  x+=1;
		if (isInvisible)  x+=2;
		if (isBridge)     x+=8;
		fos.write(x);
		
		x = 0;
		if (isDiggable)   x+=1;
		if (isStair)      x+=2;
		if (useFootprint) x+=4;
		fos.write(x);
		
		fos.write(0);
		fos.write(0);
		
		// calculate min_max values
		float min_x = point_x[points[i][0]];
		float max_x = point_x[points[i][0]];
		float min_y = point_y[points[i][0]];
		float max_y = point_y[points[i][0]];
		float min_z = height[i];
		float max_z = height[i];
		
		for (j=1;j<nbrOfVertices[i];j++) {
		  if (point_x[points[i][j]] < min_x) min_x = point_x[points[i][j]];
		  if (point_x[points[i][j]] > max_x) max_x = point_x[points[i][j]];
		  if (point_y[points[i][j]] < min_y) min_y = point_y[points[i][j]];
		  if (point_y[points[i][j]] > max_y) max_y = point_y[points[i][j]];
		}
		
		if (isStair) {
		  if (height[i] < offset[i]) {
		    max_z = offset[i];
		  } else {
		    min_z = offset[i];
		  }
		}
		
		// Recalculate the bz-value
        if (isStair && (offset_x!=0 || offset_y!=0 || scale!=1.0f) ) {
		  bz[i] = ((offset_x+point_x[points[i][0]])*kx[i] + (offset_y+point_y[points[i][0]])*ky[i] - (max_z-min_z))*-1 + min_z;
        }
		
		// write kx, ky, bz
		tmp = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("temp_sec")));
		tmp.writeFloat(kx[i]);
		tmp.writeFloat(ky[i]);
		tmp.writeFloat(bz[i]);
		
		tmp.flush();
		tmp.close();
		
		in = new FileInputStream("temp_sec");
	    byte[] mem1 = new byte[12];
	    in.read(mem1);
	    in.close();
	  
	    j=0;
	    for (k=0;k<3;k++) {
	      fos.writeByte(mem1[j+3]);
		  fos.writeByte(mem1[j+2]);
		  fos.writeByte(mem1[j+1]);
		  fos.writeByte(mem1[j]);
		  j+=4;
		}
		
		// write unknown
		for (k=0;k<24;k++) {
		  fos.write(0);
		}
		
		// write min_max values
		tmp = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("temp_sec")));
	
	    tmp.writeFloat(min_x);
		tmp.writeFloat(min_y);
		tmp.writeFloat(min_z);
		tmp.writeFloat(max_x);
		tmp.writeFloat(max_y);
		tmp.writeFloat(max_z);
		
		tmp.flush();
		tmp.close();
		
		min_x = (float)java.lang.Math.floor((min_x - MINx) / 64);
		max_x = (float)java.lang.Math.ceil((max_x - MINx)  / 64);
		min_y = (float)java.lang.Math.floor((min_y - MINy) / 64);
		max_y = (float)java.lang.Math.ceil((max_y - MINy)  / 64);
		for (k=(int)min_y;k<(int)max_y;k++) {
		  for (j=(int)min_x;j<(int)max_x;j++) {
		    int nbrOfSecs = ++grid[k*nbrOfMeshes_x+j][0];
		    grid[k*nbrOfMeshes_x+j][nbrOfSecs] = i;
			nbrOfDistricts++;
		  }
		}
		
		in = new FileInputStream("temp_sec");
	    byte[] mem2 = new byte[24];
	    in.read(mem2);
	    in.close();
	  
	    j=0;
	    for (k=0;k<6;k++) {
	      fos.writeByte(mem2[j+3]);
		  fos.writeByte(mem2[j+2]);
		  fos.writeByte(mem2[j+1]);
		  fos.writeByte(mem2[j]);
		  j+=4;
		}

		// write borders
		for (j=nbrOfVertices[i]-1;j>=0;j--) {
		  byte temp[] = {0,0,0,0};
		  temp[0] = (byte) ((border_count+j) % 256);
		  temp[1] = (byte) ((border_count+j) / 256);
		  fos.write(temp);
		}
		border_count+=nbrOfVertices[i];
	  }
	  
	  // write Tail DA
	  fos.writeBytes("2SAH");
	  byte temp[] = {0,0,0,0};
	  
	  temp[0] = (byte) ((nbrOfDistricts) % 256);
	  temp[1] = (byte) ((nbrOfDistricts) / 256);
	  fos.write(temp);
	 
	  temp[0] = 6; // unknown
      temp[1] = 0;
	  fos.write(temp);
	  
	  temp[0] = (byte) ((nbrOfMeshes_x) % 256);
	  temp[1] = (byte) ((nbrOfMeshes_x) / 256);
	  fos.write(temp);
	  
	  temp[0] = (byte) ((nbrOfMeshes_y) % 256);
	  temp[1] = (byte) ((nbrOfMeshes_y) / 256);
	  fos.write(temp);
	  
	  for (i=0;i<nbrOfMeshes_x*nbrOfMeshes_y;i++) {
	    temp[0] = (byte) ((grid[i][0]) % 256);
	    temp[1] = (byte) ((grid[i][0]) / 256);
	    fos.write(temp);
	    for (j=1;j<=grid[i][0];j++) {
		  temp[0] = (byte) ((grid[i][j]) % 256);
          temp[1] = (byte) ((grid[i][j]) / 256);
	      fos.write(temp);
		}
	  }
	  
	  fos.flush();
	  fos.close();
	  
	  System.out.println("\n  DONE!");
	  
    } catch (IOException e) {
      //e.printStackTrace();
	  System.out.println("\nERROR: Input/Output Exception!");
    } catch (InputMismatchException e) {
	  System.out.println("\nERROR: Not a valid SEC file!");
	} finally {
	  File f = new File("temp_sec");
	  if (f.exists()) {
        f.delete();
	  }
	}
  }
}