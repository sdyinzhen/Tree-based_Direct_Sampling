package clusterTreeSimulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Main_Antarctica_ClusterTreeSimulation2D_PatternBased_rank
{
	public static void main(String arg[]) throws IOException
	{
		// below are the user defined parameters for TDS
		System.out.println("Antarctica simulation: k-means cluster tree extra-search pattern-based rank-driven 2D mission begin !");
		System.out.println(new Date());
		final int RealizationAmount = 1;
		final int Height_R = 1202;
		final int Width_R = 1202;
		final int UnknownBound = -5000;
		final double MaxElementDifference = 4346;   // max elevation: 3346 min -1300
		
		// DS parameters
		final int DS_MaxRadius = 10;
		final int DS_Neighborhood = 100;
		final double DS_Threshold = 0.02;
		final double DS_Fraction = 0.1;
		final int DS_Neighborhood_min = 0;
		
		// TDS parameters
		final int Tree_Height = 9;
		// the tree search scope
		final int minimumComputation = 32;
		// pattern based TDS
		final double PointPatternSlope = 0.025;
		final int PastingArea_max = 10;
		
		// analyze the distance
		double distance_print_primary_Mean = 0.00;
		final int distance_print_Amount = 10000;
		// report distance after very $distance_print_amount points.
		int simulationPointCounter = 0;
		int searchCounter = 0;
		// above are the user defined parameters for TDS
		
		Realization2D_Antarctica realization_Original = new Realization2D_Antarctica(Height_R,Width_R);
		realization_Original.ReadRealizationFile(new String("z_Antarctica_TI_refined.txt"));
		//realization.PrintOut_Color(new String("z_original"));
		
		int unknownAmount = realization_Original.GetUnknownAmount(UnknownBound);
		System.out.println(new String("the number of unknown points is "+ unknownAmount));
		
		Realization2D_Antarctica realization = new Realization2D_Antarctica(Height_R,Width_R);
		
		final String affix = new String("z_Antarctica");
		ClusterTree tree = new ClusterTree();
		tree.ReadClusterTreeFile(affix);
		tree.ReadClusterTreeLeafNodeFile(Tree_Height, Width_R, affix);
		
		double[][] distanceImage = new double[Height_R][Width_R];
		
		int realizationIndex=0,unknownIndex=0,unknownX=0,unknownY=0,unknownValue=0;
		int currentHeight = 0,representativeX=0, representativeY=0, testClusterAmount=0;
		int solutionX=0,solutionY=0,pasteSize_y=0,pasteSize_x=0,xx=0,yy=0;
		int extraSearchAmount=0,extraSearchIndex=0,extraSearchScope=0,extraSearchRandom=0;
		int extraSearch_ClusterIndex_Deviation = (1<<Tree_Height)-2;
		double distance_min=0.0,distance=0.0,diversity=0.0;
		Random random = new Random();
		PatternDS pattern_DS = new PatternDS();
		ArrayList<Integer> testCluster = new ArrayList<Integer>();
		TreeMap<Double,Integer> testDistanceCluster= new TreeMap<Double,Integer>();
		TreeMap<Integer,Double> testClusterDistance= new TreeMap<Integer,Double>();
		TreeMap<Double,Integer> testDiversityCluster= new TreeMap<Double,Integer>();
		TreeMap<Integer,Double> testClusterPerference = new TreeMap<Integer,Double>();
		TreeMap<Double,Integer> testPerferenceCluster = new TreeMap<Double,Integer>();
		int rankClusterIndex = 0, rankCounter=0,occupyCluster = 0;
		double rank1=0.0, rank2 = 0.0;
		
		Realization2D realization_distancetoHardData = new Realization2D(Height_R,Width_R);
		for(unknownY=0;unknownY!=Height_R;++unknownY)
		{
			for(unknownX=0;unknownX!=Width_R;++unknownX)
			{
				unknownValue = realization_Original.GetValue(unknownY, unknownX);
				if(unknownValue > UnknownBound)
				{
					// the distance to hard data is zero
					realization_distancetoHardData.SetValue(unknownY, unknownX, 0);
				}
			}
		}
		realization_distancetoHardData.DilateDistance();
		// realization_distancetoHardData.PrintOut_Color(new String("z_distanceData"));
		
		// record the running time
		Date end = new Date();
		int CostTime = 0;
		
		// record the searching time
		long searchingTime_start = 0, searchingTime_end=0, searchingTime=0;
		
		// 2D simulations
		System.out.println("2D simulations begin !");
		System.out.println(new Date());
		Date start = new Date();
		for(realizationIndex=0;realizationIndex!=RealizationAmount;++realizationIndex)
		{
			System.out.println(new String("realization"+realizationIndex));
			realization.SetRealizationMatrix(realization_Original.GetRealizationMatrix());
			unknownIndex = 0;
			searchCounter = 0;
			distance_print_primary_Mean = 0.0;
			simulationPointCounter = 0;
			while(unknownIndex < unknownAmount)
			{
				unknownY = Math.abs(random.nextInt()%Height_R);
				unknownX = Math.abs(random.nextInt()%Width_R);
				if(realization.GetValue(unknownY, unknownX) < UnknownBound)
				{
					pattern_DS = new PatternDS(realization.GetPattern_DS(unknownY, unknownX, DS_Neighborhood, DS_MaxRadius, UnknownBound));
					if(pattern_DS.GetKnownPointAmount() <= DS_Neighborhood_min)
					{
						continue;
					}
					else
					{
						// System.out.println(unknownIndex);
						// initialization
						distanceImage = new double[Height_R][Width_R];
						testCluster.clear();
						testCluster.add(0);
						testCluster.add(1);
						
						// record the searching time
						searchingTime_start = System.nanoTime();
						
						unknownValue = -5001;
						distance_min = 1.0;
						for(currentHeight=1;currentHeight<=Tree_Height;++currentHeight)
						{
							testDistanceCluster.clear();
							testDiversityCluster.clear();
							testClusterDistance.clear();
							for(int testClusterIndex : testCluster)
							{
								// get the coordinate of representative
								representativeX = tree.GetCoordinate_X(testClusterIndex);
								representativeY = tree.GetCoordinate_Y(testClusterIndex);
								
								// calculate the distance between training pattern and conditioning pattern
								if(distanceImage[representativeY][representativeX]>0.0)
								{
									distance = distanceImage[representativeY][representativeX];
								}
								else
								{
									distance = realization.GetPatternDistance_Euclidean(representativeY, representativeX, pattern_DS, UnknownBound, MaxElementDifference);
								}
								
								// is the training pattern satisfied?
								if(distance <= DS_Threshold)
								{
									unknownValue = realization.GetValue(representativeY, representativeX);
									solutionY = representativeY;
									solutionX = representativeX;
									distance_min = distance;
									break;
								}
								else if (distance < distance_min)
								{
									solutionY = representativeY;
									solutionX = representativeX;
									distance_min = distance;
								}
								distanceImage[representativeY][representativeX] = distance;
								
								// calculate the preference (also termed indicator)
								while(true)
								{
									if(testDistanceCluster.containsKey(distance))
										distance = distance-0.0000000001;
									else
										break;
								}
								testDistanceCluster.put(distance, testClusterIndex);
								testClusterDistance.put(testClusterIndex, distance);
								diversity = 0-tree.GetInnerDistance(testClusterIndex);
								while(true)
								{
									if(testDiversityCluster.containsKey(diversity))
									{
										diversity = diversity-0.0000000001;
									}
									else
									{
										break;
									}
								}
								testDiversityCluster.put(diversity, testClusterIndex);
							}
							if(unknownValue > UnknownBound)
							{
								break;
							}
							else if (currentHeight!=Tree_Height)
							{
								// proceed to the next layer of tree
								testClusterPerference.clear();   // cluster : rank
								testPerferenceCluster.clear();   // rank : cluster
								rank1 = 0.0;
								for(int mapvalue : testDistanceCluster.values())
								{
									testClusterPerference.put(mapvalue, rank1);
									++rank1;
								}
								rank2 = 0.0;
								for(int mapvalue : testDiversityCluster.values())
								{
									rank1= testClusterPerference.get(mapvalue);
									rank1 = (Tree_Height-currentHeight)/(Tree_Height-1)*rank1 + (currentHeight-1)/(Tree_Height-1)*rank2;
									testClusterPerference.put(mapvalue, rank1);
									++rank2;
								}
								
								for (Map.Entry<Integer,Double> entry : testClusterPerference.entrySet())
								{
									rankClusterIndex = entry.getKey();
									rank1 = entry.getValue();
									
									if(testPerferenceCluster.containsKey(rank1))
									{
										rankCounter = 1;
										while(true)
										{
											occupyCluster = testPerferenceCluster.get(rank1);
											if(testClusterDistance.get(rankClusterIndex) < testClusterDistance.get(occupyCluster))
											{
												rank1 = rank1 - Math.pow(0.5, (double)rankCounter);
												++rankCounter;
											}
											else
											{
												rank1 = rank1 + Math.pow(0.5, (double)rankCounter);
												++rankCounter;
											}
											if(testPerferenceCluster.containsKey(rank1))
												continue;
											else
												break;
										}
									}
									testPerferenceCluster.put(rank1, rankClusterIndex);
								}
								
								testClusterAmount = Math.max(minimumComputation,testCluster.size());
								testCluster.clear();
								for(int mapvalue : testPerferenceCluster.values())
								{
									testCluster.add(2*mapvalue+2);
									testCluster.add(2*mapvalue+3);
									if(testCluster.size()==testClusterAmount)
										break;
								}
							}
						}
						// carry out the extra search
						if(unknownValue < UnknownBound)
						{
							for(int testClusterIndex : testCluster)
							{
								extraSearchAmount = tree.GetMemberPatternSize(testClusterIndex-extraSearch_ClusterIndex_Deviation);
								extraSearchScope = (int)((double)extraSearchAmount * DS_Fraction);
								for(extraSearchIndex=0;extraSearchIndex!=extraSearchScope;++extraSearchIndex)
								{
									extraSearchRandom = Math.abs(random.nextInt()%extraSearchAmount);
									representativeX = tree.GetMemberPattern_X(testClusterIndex-extraSearch_ClusterIndex_Deviation, extraSearchRandom);
									representativeY = tree.GetMemberPattern_Y(testClusterIndex-extraSearch_ClusterIndex_Deviation, extraSearchRandom);
									if(distanceImage[representativeY][representativeX]>0.0)
										continue;
									else
									{
										distance = realization.GetPatternDistance_Euclidean(representativeY, representativeX, pattern_DS, UnknownBound, MaxElementDifference);
										// is the training pattern satisfied?
										if(distance <= DS_Threshold)
										{
											unknownValue = realization.GetValue(representativeY, representativeX);
											solutionY = representativeY;
											solutionX = representativeX;
											distance_min = distance;
											break;
										}
										else if (distance < distance_min)
										{
											solutionY = representativeY;
											solutionX = representativeX;
											distance_min = distance;
										}
										distanceImage[representativeY][representativeX] = distance;
									}
								}
							}
						}
						
						// record the searching time
						searchingTime_end = System.nanoTime();
						searchingTime = searchingTime + searchingTime_end - searchingTime_start;
						
						// analyze the resulting distance
						distance_print_primary_Mean += distance_min;
						++searchCounter;
						if((unknownIndex - simulationPointCounter) > distance_print_Amount)
						{
							System.out.println(distance_print_primary_Mean/(double)searchCounter);
							distance_print_primary_Mean = 0.0;
							searchCounter = 0;
							simulationPointCounter = unknownIndex;
						}
						
						// paste a pattern
						pasteSize_y = (int)((double)(realization_distancetoHardData.GetValue(unknownY, unknownX)) * (double)(PointPatternSlope));
						// pasteSize_x = (int)((double)(realization_distancetoHardData.GetValue(unknownY, unknownX)) * (double)(PointPatternSlope));
						if(pasteSize_y > PastingArea_max)
							pasteSize_y = PastingArea_max;
						pasteSize_x = pasteSize_y;
						
						for(yy=-pasteSize_y;yy!=pasteSize_y+1;++yy)
						{
							for(xx=-pasteSize_x;xx!=pasteSize_x+1;++xx)
							{
								if(unknownY+yy>=0 && unknownY+yy<Height_R && unknownX+xx>=0 && unknownX+xx<Width_R)
								{
									if(realization.GetValue(unknownY+yy, unknownX+xx)> UnknownBound)
									{
										continue;
									}
									if(solutionY+yy>=0 && solutionY+yy<Height_R && solutionX+xx>=0 && solutionX+xx<Width_R)
									{
										unknownValue = realization.GetValue(solutionY+yy, solutionX+xx);
										realization.SetValue(unknownY+yy, unknownX+xx, unknownValue);
										++unknownIndex;
									}
								}
							}
						}
					}
				}
			}
			//realization.PrintOut_Color(new String("z_original"));
			realization.PrintOut_txt(new String("./tds_output/z_Antarctica_PatternTDS_Realization"+realizationIndex+".txt"));
			end = new Date();
			CostTime = (int) (end.getTime()-start.getTime());
			System.out.println(new String("The program costs "+ CostTime + " micro-seconds so far"));
			CostTime = (int) (end.getTime()-start.getTime())/1000;
			System.out.println(new String("The program costs "+ CostTime + " seconds so far"));
			
			System.out.println(new String("The searching program costs "+ searchingTime + " nanoseconds so far"));
			System.out.println(new String("The searching program costs "+ searchingTime * 1e-9  + " seconds so far"));
		}
		
		System.out.println(new Date());
		end = new Date();
		CostTime = (int) (end.getTime()-start.getTime())/1000;
		System.out.println(new String("The simulation procedure costs "+ CostTime + " seconds"));
		
		System.out.println(new String("The searching program costs "+ searchingTime + " nanoseconds so far"));
		System.out.println(new String("The searching program costs "+ searchingTime * 1e-9  + " seconds so far"));
		
		System.out.println(new String("The whole procedure have been accomplished !"));
		return;
	}
}
