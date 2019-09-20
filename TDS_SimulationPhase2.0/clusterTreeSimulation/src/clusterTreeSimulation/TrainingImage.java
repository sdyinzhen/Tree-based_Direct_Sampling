package clusterTreeSimulation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TrainingImage 
{
	public TrainingImage()
	{
		imagedata0 = new int[0][0];
		height = 0;
		width = 0;
		return;
	}
	public void ReadCategoricalImage(String str)
	{
		int y=0,x=0,i=0;
		try {
			image0 = ImageIO.read(new File(str));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		height=image0.getHeight();
		width =image0.getWidth();
	
		imagedata0 = new int[height][width];
		
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				i=image0.getRGB(x,y);				
				if(i==-16777216)   //black
				{
					imagedata0[y][x]=0;
				}
				else if(i==-1)   //white
				{
					imagedata0[y][x]=1;
				}
				else if(i==-65536)  //red
				{
					imagedata0[y][x]=2;
				}
				else if(i==-16776961)  //blue
				{
					imagedata0[y][x]=3;
				}
				else if(i==-16711936)  //green
				{
					imagedata0[y][x]=4;
				}
				else 
				{
					imagedata0[y][x]=-1;
				}
			}
		}		
		System.out.println("Categorical image reading mission is finished"+" Height:"+height+" width:"+width);
	}
	public void PrintOut_categorical(String str)
	{
		int y=0,x=0,i=0;
		
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);		
		
		for(y=0;y!=height;++y)
		{
			for(x=0;x!=width;++x)
			{
				i=imagedata0[y][x];
				if(i==0)
				    image.setRGB(x, y,-16777216);   //black
				else if(i==1)
					image.setRGB(x,y,-1);  // white
				else if(i==2)
					image.setRGB(x,y,-65536);   //red
				else if(i==3)
					image.setRGB(x,y,-16776961);   //blue
				else if(i==4)
					image.setRGB(x,y,-16711936);   //green
				else 
					image.setRGB(x,y,-8355712);   //gray
			}
		}
		str = str+".bmp";
		File f = new File(str);
		
		try {
			ImageIO.write(image,"bmp",f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	public int GetHeight()
	{
		return height;
	}
	public int GetWidth()
	{
		return width;
	}
	public int GetValue(int y,int x)
	{
		return imagedata0[y][x];
	}
	public double GetPatternDistance_Hamming(int PositionY,int PositionX,PatternDS pattern)
	{
		double distance = 0;
		int index = 0,y=0,x=0;
		int Neighborhood = pattern.GetSize();
		for(index=0;index!=Neighborhood;++index)
		{
			y = PositionY + pattern.GetCoordinateY(index);
			x = PositionX + pattern.GetCoordinateX(index);
			
			if(y<0 || y>=height || x<0 || x>=width)
			{
				++distance;
			}
			else if(imagedata0[y][x]!=pattern.GetPatternValue(index))
			{
				++distance;
			}
		}
		distance = distance / (double)Neighborhood;
		return distance;
	}
	private BufferedImage image0;
	private int[][] imagedata0;
	private int height;
	private int width;
}
