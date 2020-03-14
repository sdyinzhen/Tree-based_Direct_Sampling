# TDS: Tree-based Direct Sampling (on 2D Topography modeling)
Code Reference - 
* [Zuo, C., Yin, Z., Pan, Z., MacKie, E. J., & Caers, J. ( 2020). A tree‐based direct sampling method for stochastic surface and subsurface hydrological modeling. Water Resources Research, 56, e2019WR026130.](https://agupubs.onlinelibrary.wiley.com/doi/abs/10.1029/2019WR026130/)

<p> This is the first version of the Tree-based Direct Sampling (TDS), with 2D Antarctica Topography modeling case as example. 

### Structure of the TDS setup
The TDS has two steps:
* Step 1: Training Phase, run under directory [TDS_TrainingPhase](https://github.com/sdyinzhen/Tree-based_Direct_Sampling/tree/master/TDS_TrainingPhase). This step builds a tree using Training Image (TI)
* Step 2: Simulation Phase, run under [TDS_SimulationPhase2.0](https://github.com/sdyinzhen/Tree-based_Direct_Sampling/tree/master/TDS_SimulationPhase2.0). This step simulates direct sampling realizations using the tree built at Step 1. 
### Install and Run TDS
Please follow the provided [User Manual](https://github.com/sdyinzhen/Tree-based_Direct_Sampling/blob/master/UserManual_Tree-basedDirectSampling.pdf) to install and run TDS

To run the TDS, the following dependencies must be met:
* [Jupyter](http://jupyter.org/) 
* [Python 3.7](https://www.python.org/) 
* We recommend to install the above two together via [Anaconda](https://www.anaconda.com/).
* [Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Eclipse IDE for Java](https://www.eclipse.org/downloads/)

### Tutorial
A Tutorial example is provided in the [User Manual](https://github.com/sdyinzhen/Tree-based_Direct_Sampling/blob/master/UserManual_Tree-basedDirectSampling.pdf). 

### Contact
Please contact [David Zhen Yin](mailto:yinzhen@stanford.edu) or [Chen Zuo](mailto:chenzuo789@outlook.com) in case of questions. 

### Licensing
This repository is released under the MIT License.
