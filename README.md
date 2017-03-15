# secdepvne
Secure and dependable virtual network embedding

Author: Luis Ferrolho

Clean all the files with cleanAll.sh

Execute the main by passing to it the following arguments: \<random> \<number of substrate nodes> \<number of virtual network requests> \<number of clouds>
Example: java Main random 25 1000 3

Note: If networks are not pre-generated uncomment the code lines with a TODO tag.


After the end of the simulation execute DatCreator to obtain the files to plot, passing to it the following arguments: \<number of substrate nodes>
Example: java DatCreator 25

Execute our plot.p to obtain the plots: gnuplot plot.p
