#Author: Chen Zuo and David Zhen Yin
#Contact: chenzuo789@outlook.com; yinzhen@stanford.edu
#Date: September 19, 2019

import matplotlib.pyplot as plt
import numpy as np
import time
import datetime
from scipy import interpolate
from PIL import Image
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans

import matplotlib.pyplot as plt
import numpy as np
import time
import datetime
from scipy import interpolate
from PIL import Image
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans

import matplotlib.pyplot as plt
import numpy as np
import time
import datetime
from scipy import interpolate
from PIL import Image
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans

def build_TI_tree(project_name, TraingImage, TraingImage_vec,Realization_Height, Realization_Width,  ArtificalThreshold, \
               template_radius_x, template_radius_y, TreeHeight_max, knownUpbound, knownBelowbound):
    '''This is the function to constructed a tree using training image. Main arguements are:
    project_name: the name of the project, format - strings
    TraingImage: training image (TI), format - 2Darray
    TraingImage_vec: vectorized array of the training image, format - 1Darray
    Realization_Height: dimension of the 2D training image in y direction, format - int
    Realization_Width: dimenison of the 2D training image in x direction, format - int
    ArtificalThreshold: threshold values to elimate areas on the training image, format - float
    template_radius_x: searching radius in x direction, format - int
    template_radius_y: searching radius in y dierection, format - int
    TreeHeight_max: maximum height of the tree, format - int
    knownUpbound:maximum value of known TI attribute
    knownBelowbound: minimum value of known TI attribute
    '''
    print("mission begins !")
    print((datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')))
    # start time 0
    start_time0 = time.time()
    # find the simulation area
    PointY = 0
    PointX = 0
    value = 0
    KnownArea_row = np.zeros(Realization_Height*Realization_Width)
    KnownArea_row[np.where(TraingImage_vec > knownBelowbound)[0]] = 1

    # artificially eliminate the occean area on the top right corner
    ArtificalKnownArea_row =  np.ones(shape=Realization_Height*Realization_Width)
    ArtificalKnownArea_row[ np.where(TraingImage_vec < ArtificalThreshold)[0] ] = 0
    ArtificalKnownArea_2D = ArtificalKnownArea_row.reshape(Realization_Height,Realization_Width)
    plt.imshow(ArtificalKnownArea_2D,cmap = 'jet')
    plt.title('training area after artifical manipulation')
    plt.colorbar()
    # we only process the red area

    # start time 1
    start_time1 = time.time()
    # extract all pattern

    ExtractPattern_Y = []
    ExtractPattern_X = []

    PatternSize = (2*template_radius_y+1)*(2*template_radius_x+1)
    PatternAmount = 0
    # find the location can be used to extract training patterns
    for point_y in range(template_radius_y,Realization_Height-template_radius_y):
        for point_x in range(template_radius_x,Realization_Width-template_radius_x):
            vector = TraingImage[(point_y-template_radius_y):(point_y+template_radius_y+1), (point_x-template_radius_x):(point_x+template_radius_x+1)]
            vector = vector.reshape((1,PatternSize))
            if( len(vector[vector < knownBelowbound])==0):
                vector = ArtificalKnownArea_2D[(point_y-template_radius_y):(point_y+template_radius_y+1), (point_x-template_radius_x):(point_x+template_radius_x+1)]
                vector = vector.reshape((1,PatternSize))
                if( len(vector[vector < 1])==0):
                    PatternAmount += 1
                    ExtractPattern_Y.append(point_y)
                    ExtractPattern_X.append(point_x)

    print("The amount of pattern is "+str(PatternAmount))
    print("The size of pattern is "+str(PatternSize))
    ExtractPattern_Y = np.array(ExtractPattern_Y)
    ExtractPattern_X = np.array(ExtractPattern_X)

    PatternBase = np.empty((PatternAmount,PatternSize))
    index_Pattern = 0
    for point_y, point_x in zip(ExtractPattern_Y,ExtractPattern_X):
        vector = TraingImage[(point_y-template_radius_y):(point_y+template_radius_y+1), (point_x-template_radius_x):(point_x+template_radius_x+1)]
        vector = vector.reshape((1,PatternSize))
        PatternBase[index_Pattern] = vector
        index_Pattern += 1

    extractMap = np.full(shape=(Realization_Height,Realization_Width),fill_value=-1)
    for point_y, point_x in zip(ExtractPattern_Y,ExtractPattern_X):
        extractMap[point_y][point_x] = 1
    
    #perform the PCA to calculate the variance distribution and transform the previous data to new feature space
    print('running PCA')
    EnergyThreshold = 0.95

    pca = PCA(n_components=PatternSize)
    pca.fit(PatternBase)
    varianceVector = pca.explained_variance_ratio_

    PatternBase_Decompose_All = pca.transform(PatternBase)

    cumulativeVarianceVector = np.cumsum(varianceVector)
    SelectDimension = np.searchsorted(cumulativeVarianceVector, EnergyThreshold)+1
    # print('the cumulative variance is')
    # for variance in cumulativeVarianceVector:
    #     print(str(variance))
    print("The reduced dimension is "+str(SelectDimension))

    fig, ax = plt.subplots()
    ax.plot(np.linspace(1,PatternSize,PatternSize),cumulativeVarianceVector,color='r',linewidth=2,label='cumulative Variance')
    ax.legend(loc='upper right')
    ax.set_yticks(np.linspace(0,1.1,12))
    ax.set_ylabel('cumulative variance')
    ax.set_xlabel('dimension')
    plt.show()
    
    # # display the point cloud distribution. A point corresponds to a pattern.
    plt.scatter(PatternBase_Decompose_All[:,0],PatternBase_Decompose_All[:,1], cmap='viridis_r',edgecolors='k', linewidth =0.25)
    plt.show()
    
    Pattern_Decompose_Partial = PatternBase_Decompose_All[0:PatternAmount,0:SelectDimension]
    
    # calculate the biggest distance within the pattern set
    MaxInnerRadius = 0;

    #find the center of all patterns
    kmeans_1 = KMeans(n_clusters= 1, random_state=0).fit(Pattern_Decompose_Partial);
    KmeansCenter_1 = kmeans_1.cluster_centers_
    euclideanColumnVector = np.linalg.norm(Pattern_Decompose_Partial-KmeansCenter_1,axis=1)
    MaxInnerRadius = np.max(euclideanColumnVector)
    print('the maximum inner radius is '+ str(MaxInnerRadius))
    
    
    
    # bulid the cluster tree

    Representative_Y = []
    Representative_X = []
    InnerRadius = []

    treeHeight = 1

    clusterAmount_old = 1
    clusterIndex_old = 0
    clusterAmount_new = 2

    clusterResult_old = np.ones(PatternAmount).astype(int)

    memberPatternIndex_old = np.linspace(0,PatternAmount-1,PatternAmount).reshape(1,PatternAmount)
    memberPatternIndex_old = memberPatternIndex_old.astype(int)

    KmeansResult = np.ones(PatternAmount)

    MaxClusterSize = PatternAmount

    for treeHeight in range(1,TreeHeight_max+1):
        print('height: '+str(treeHeight))
        clusterAmount_old = 2**(treeHeight-1)
        clusterAmount_new = 2**treeHeight
        print('cluster amount:'+str(clusterAmount_new))

        memberPatternIndex_new = np.full(shape=(clusterAmount_new,MaxClusterSize),fill_value=-1)
        clusterSize = np.empty(clusterAmount_new)

        for clusterIndex_old in range(clusterAmount_old):

            memberPatternIndex_current = memberPatternIndex_old[clusterIndex_old]
            memberPatternIndex_current = memberPatternIndex_current[memberPatternIndex_current > -1]
            # print(memberPatternIndex_current)
            if(len(memberPatternIndex_current)==0):   # solve empty cluster
                for clusterIndex_child in range(0,2):
                    clusterSize[clusterIndex_old*2+clusterIndex_child] = 0
                    Representative_X.append( -1 )
                    Representative_Y.append( -1 ) 
                    InnerRadius.append( -1.0 )
                continue
            elif(len(memberPatternIndex_current)==1):
                clusterSize[clusterIndex_old*2] = 1
                index_Pattern = (int)(len(Representative_X)/2 - 1)
                print('the len of current cluster is '+str(index_Pattern))
                Representative_X.append( Representative_X[index_Pattern] )
                Representative_Y.append( Representative_Y[index_Pattern] ) 
                InnerRadius.append( InnerRadius[index_Pattern] )

                clusterSize[clusterIndex_old*2+1] = 0
                Representative_X.append( -1 )
                Representative_Y.append( -1 ) 
                InnerRadius.append( -1.0 )
                continue
            currentPatternBase = Pattern_Decompose_Partial[memberPatternIndex_current]
            # print('the shape of current clustering PCA matrix is '+str(currentPatternBase.shape))

            kmeans = KMeans(n_clusters= 2, random_state=0).fit(currentPatternBase);
            KmeansCenter = kmeans.cluster_centers_  # two centers
            KmeansResult_temporary = kmeans.labels_  # the current cluster result

            for clusterIndex_child in range(0,2):
                # assign the index of the first cluster to the new vector
                memberPatternIndex_current = np.where(KmeansResult_temporary == clusterIndex_child)[0]
                clusterSize[clusterIndex_old*2+clusterIndex_child] = len(memberPatternIndex_current)
                memberPatternIndex_current = memberPatternIndex_old[clusterIndex_old][memberPatternIndex_current]
                KmeansResult[memberPatternIndex_current] = clusterIndex_old*2 + clusterIndex_child
                memberPatternIndex_current_pad = np.pad(memberPatternIndex_current,pad_width=(0,MaxClusterSize-len(memberPatternIndex_current)),mode='constant',constant_values=(0,-1))
                memberPatternIndex_new[clusterIndex_old*2+clusterIndex_child] = memberPatternIndex_current_pad

                if(len(memberPatternIndex_current) == 0):   # solve empty cluster
                    # there is no pattern in current cluster
                    Representative_X.append( -1 )
                    Representative_Y.append( -1 ) 
                    InnerRadius.append( -1.0 )
                else:
                    # find the actual patterns are closest to first center 
                    euclideanRowVector = np.linalg.norm(Pattern_Decompose_Partial-KmeansCenter[clusterIndex_child],axis=1)
                    representative = np.argmin(euclideanRowVector)
                    Representative_X.append( ExtractPattern_X[representative] )
                    Representative_Y.append( ExtractPattern_Y[representative] ) 
                    # calculate the inner radius
                    InnerRadius.append( np.mean(euclideanRowVector[memberPatternIndex_current]) / MaxInnerRadius )


        memberPatternIndex_old = memberPatternIndex_new.copy()

        # statistically analyze the size of clusters
        print('the average size: '+ str(np.mean(clusterSize)))
        MaxClusterSize = np.max(clusterSize).astype(int)
        print('the max size: '+ str(MaxClusterSize))
        print('the min size: '+ str(np.min(clusterSize)))

    #     # display the cluster result


        extractMap = np.full(shape=(Realization_Height,Realization_Width),fill_value=-1)
        for point_y, point_x,cluster in zip(ExtractPattern_Y,ExtractPattern_X,KmeansResult):
            extractMap[point_y][point_x] = cluster
        plt.imshow(extractMap,cmap = 'jet')
        plt.title('the locations of training patterns')
        plt.colorbar()
        plt.show()

    #     # print the histogram of cluster size
        plt.bar(np.linspace(0,clusterAmount_new-1,clusterAmount_new), clusterSize, width = 0.8)
        plt.title('the histogram of cluster size')
        plt.show()
        
        # end time 0
    end_time0 = time.time()
    
    # output the file
    Representative_Y = np.array(Representative_Y).reshape(2*clusterAmount_new-2)
    Representative_X = np.array(Representative_X).reshape(2*clusterAmount_new-2)
    InnerRadius = np.array(InnerRadius).reshape(2*clusterAmount_new-2)
    # print(Representative_Y)
    file_Y = open("./../TDS_SimulationPhase2.0/clusterTreeSimulation/z_"+project_name+"_python_clusterTree_Representative_Y.txt","w")
    file_X = open("./../TDS_SimulationPhase2.0/clusterTreeSimulation/z_"+project_name+"_python_clusterTree_Representative_X.txt","w")
    file_Radius = open("./../TDS_SimulationPhase2.0/clusterTreeSimulation/z_"+project_name+"_python_cluster_AverageDistance.txt","w")
    for x,y,radius in zip(Representative_X,Representative_Y,InnerRadius):
        file_X.write(str(x)+'\n')
        file_Y.write(str(y)+'\n')
        file_Radius.write(str(radius)+'\n')
    file_Y.close()
    file_X.close()
    file_Radius.close()

    extractMap = np.full(shape=(Realization_Height,Realization_Width),fill_value=-1)
    for point_y, point_x,cluster in zip(ExtractPattern_Y,ExtractPattern_X,KmeansResult):
        extractMap[point_y][point_x] = cluster
    KmeansResult_Image = extractMap.reshape(Realization_Height*Realization_Width)
    file_KmeansResult = open("./../TDS_SimulationPhase2.0/clusterTreeSimulation/z_"+project_name+"_python_clusterTree_Result.txt","w")
    for cluster in KmeansResult_Image:
        file_KmeansResult.write(str(cluster)+'\n')
    file_KmeansResult.close()
    
    # end time 1
    end_time1 = time.time()
    
    print('the cluster tree construction costs '+str(end_time0 - start_time1)+' seconds')
    print('the cluster tree construction and file outputs take '+str(end_time1 - start_time1)+' seconds')
    print('1 Read training image; 2 the cluster tree construction; 3 file outputs. All missions consume '+str(end_time1 - start_time0)+' seconds')