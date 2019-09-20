package clusterTreeSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClusterTree 
{
	public ClusterTree()
	{
		clusterTree_Y = new ArrayList<Integer>();
		clusterTree_X = new ArrayList<Integer>();
		clusterTree_InnerDistance = new ArrayList<Double>();
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
	public void ReadClusterTreeLeafNodeFile(int treeHeight,int TIWidth,String affix) throws IOException
	{
		int cluster = 0,pointX=0,pointY=0,lineIndex=0;
		int clusterAmount = 1<<treeHeight;
		for(cluster=0;cluster!=clusterAmount;++cluster)
		{
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
	            	pointY = lineIndex / TIWidth;
		            pointX = lineIndex % TIWidth;
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
		if(index >= clusterTree_X.size())
		{
			System.out.println(new String("transcend the tree height!"));
		}
		return clusterTree_X.get(index);
	}
	public int GetCoordinate_Y(int index)
	{
		return clusterTree_Y.get(index);
	}
	public double GetInnerDistance(int index)
	{
		return clusterTree_InnerDistance.get(index);
	}
	public int GetMemberPatternSize(int cluster)
	{
		return clusterMemberPattern_Y.get(cluster).size();
	}
	public int GetMemberPattern_Y(int cluster,int index)
	{
		return clusterMemberPattern_Y.get(cluster).get(index);
	}
	public int GetMemberPattern_X(int cluster,int index)
	{
		return clusterMemberPattern_X.get(cluster).get(index);
	}
	private ArrayList<Integer> clusterTree_Y;
	private ArrayList<Integer> clusterTree_X;
	private ArrayList<Double> clusterTree_InnerDistance;
	private List< List<Integer> > clusterMemberPattern_Y;  
	private List< List<Integer> > clusterMemberPattern_X;
}
