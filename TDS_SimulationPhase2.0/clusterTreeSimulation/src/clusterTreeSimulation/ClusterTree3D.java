package clusterTreeSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClusterTree3D
{
	public ClusterTree3D()
	{
		clusterTree_Z = new ArrayList<Integer>();
		clusterTree_Y = new ArrayList<Integer>();
		clusterTree_X = new ArrayList<Integer>();
		clusterTree_InnerDistance = new ArrayList<Double>();
		clusterMemberPattern_Z = new ArrayList< List<Integer> >();
		clusterMemberPattern_Y = new ArrayList< List<Integer> >();
		clusterMemberPattern_X = new ArrayList< List<Integer> >();
		return;
	}
	public void ReadClusterTreeFile(String affix) throws IOException
	{
		String str_fileX = new String(affix+"_python_clusterTree_Representative_X.txt");
		File file_fileX = new File(str_fileX);
		String str_fileY = new String(affix+"_python_clusterTree_Representative_Y.txt");
		File file_fileY = new File(str_fileY);
		String str_fileZ = new String(affix+"_python_clusterTree_Representative_Z.txt");
		File file_fileZ = new File(str_fileZ);
		String str_fileInner = new String(affix+"_python_cluster_AverageDistance.txt");
		File file_fileInner = new File(str_fileInner);
		
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file_fileX));
        BufferedReader br = new BufferedReader(reader); 
        String line = "";
        while (line != null)
        {
			line = br.readLine();
            if(line != null)
            {
	            clusterTree_X.add( Integer.valueOf(line).intValue() );
            }
        }
                
        reader = new InputStreamReader(new FileInputStream(file_fileY));
        br = new BufferedReader(reader); 
        line = "";
        while (line != null)
        {
			line = br.readLine();
            if(line != null)
            {
	            clusterTree_Y.add( Integer.valueOf(line).intValue() );
            }
        }
        
        reader = new InputStreamReader(new FileInputStream(file_fileZ));
        br = new BufferedReader(reader); 
        line = "";
        while (line != null)
        {
			line = br.readLine();
            if(line != null)
            {
	            clusterTree_Z.add( Integer.valueOf(line).intValue() );
            }
        }
        
        reader = new InputStreamReader(new FileInputStream(file_fileInner));
        br = new BufferedReader(reader); 
        line = "";
        while (line != null)
        {
			line = br.readLine();
            if(line != null)
            {
	            clusterTree_InnerDistance.add( Double.valueOf(line).doubleValue() );
            }
        }
		br.close();
		System.out.println(new String("Read all cluster representatives"));
		return;
	}
	public void ReadClusterTreeLeafNodeFile(int treeHeight,int TIHeight, int TIWidth,String affix) throws IOException
	{
		int cluster = 0,pointZ=0, pointX=0,pointY=0,lineIndex=0;
		int clusterAmount = 1<<treeHeight;
		int PointNumberPerLayer = TIWidth * TIHeight;
		for(cluster=0;cluster!=clusterAmount;++cluster)
		{
			clusterMemberPattern_Z.add( new ArrayList<Integer>() );
			clusterMemberPattern_Y.add( new ArrayList<Integer>() );
			clusterMemberPattern_X.add( new ArrayList<Integer>() );
		}
		
		String str_file = new String(affix+"_python_clusterTree_Result.txt");
		File file_file = new File(str_file);
		
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file_file));
        BufferedReader br = new BufferedReader(reader); 
        String line = "";
        while (line != null)
        {
			line = br.readLine();
            if(line != null)
            {
	            cluster = Integer.valueOf(line).intValue();
	            if(cluster!=-1)
	            {
	            	pointZ = lineIndex / PointNumberPerLayer;
	            	pointY = (lineIndex % PointNumberPerLayer) / TIWidth;
		            pointX = lineIndex % TIWidth;
		            clusterMemberPattern_Z.get(cluster).add(pointZ);
		            clusterMemberPattern_Y.get(cluster).add(pointY);
		            clusterMemberPattern_X.get(cluster).add(pointX);
	            }
	            ++lineIndex;
            }
        }      
		br.close();
		System.out.println(new String("Read all cluster member patterns"));
		return;
	}
	public int GetCoordinate_X(int index)
	{
		return clusterTree_X.get(index);
	}
	public int GetCoordinate_Y(int index)
	{
		return clusterTree_Y.get(index);
	}
	public int GetCoordinate_Z(int index)
	{
		return clusterTree_Z.get(index);
	}
	public double GetInnerDistance(int index)
	{
		return clusterTree_InnerDistance.get(index);
	}
	public int GetMemberPatternSize(int cluster)
	{
		return clusterMemberPattern_Y.get(cluster).size();
	}
	public int GetMemberPattern_Z(int cluster,int index)
	{
		return clusterMemberPattern_Z.get(cluster).get(index);
	}
	public int GetMemberPattern_Y(int cluster,int index)
	{
		return clusterMemberPattern_Y.get(cluster).get(index);
	}
	public int GetMemberPattern_X(int cluster,int index)
	{
		return clusterMemberPattern_X.get(cluster).get(index);
	}
	private ArrayList<Integer> clusterTree_Z;
	private ArrayList<Integer> clusterTree_Y;
	private ArrayList<Integer> clusterTree_X;
	private ArrayList<Double> clusterTree_InnerDistance;
	private List< List<Integer> > clusterMemberPattern_Z;
	private List< List<Integer> > clusterMemberPattern_Y;  
	private List< List<Integer> > clusterMemberPattern_X;
}
