set terminal postscript enhanced color "Helvetica" 8
set terminal pngcairo dashed
set output "AcceptanceRatio.png"
#set size ratio 0.5
#set size 0.5, 0.5
set xlabel "Time"
set ylabel "VN request acceptance ratio"
set xrange [0:50000]
set yrange [0:1]#[0.23:0.7]
set key on right bottom
plot  'DViNE.dat' using 1:6  with lines linewidth 2 lc 7 title 'D-ViNE', 'SecDep_exp1.dat' using 1:6  with lines linewidth 2 lc 1 title 'SecDep0', \
'SecDep_exp3.dat' using 1:6  with lines linewidth 2 lc 2 title 'SecDep10', 'SecDep_exp4.dat' using 1:6  with lines linewidth 2 lc 3 title 'SecDep30', \
'SecDep_exp5.dat' using 1:6  with lines linewidth 2 lc 4 title 'SecDep50', 'SecDep_exp6.dat' using 1:6  with lines linewidth 2 lc 5 title 'SecDep100', \
'SecDep_exp7.dat' using 1:6  with lines linewidth 2 lc 18 title 'MixedSecDep'
set terminal postscript enhanced color "Helvetica" 8
#set terminal png
set terminal pngcairo dashed
set output "AverageTimeRevenue.png"
#set size 0.5, 0.5
set xlabel "Time"
set ylabel "Average Revenue"
set xrange [0:50000]
set yrange [0:2]
plot 'DViNE.dat' using 1:2  with lines linewidth 2 lc 7 title 'D-ViNE', 'SecDep_exp1.dat' using 1:2 with lines linewidth 2 lc 1 title 'SecDep0', \
'SecDep_exp3.dat' using 1:2  with lines linewidth 2 lc 2 title 'SecDep10', 'SecDep_exp4.dat' using 1:2  with lines linewidth 2 lc 3 title 'SecDep30', \
'SecDep_exp5.dat' using 1:2  with lines linewidth 2 lc 4 title 'SecDep50', 'SecDep_exp6.dat' using 1:2  with lines linewidth 2 lc 5 title 'SecDep100', \
'SecDep_exp7.dat' using 1:2  with lines linewidth 2 lc 18 title 'MixedSecDep'
set terminal postscript enhanced color "Helvetica" 8
#set terminal png
set terminal pngcairo dashed
set output "AverageCost.png"
#set size 0.5, 0.5
set xlabel "Time"
set ylabel "Average Cost"
set xrange [0:50000]
set yrange [0:240]
plot 'DViNE.dat' using 1:3  with lines linewidth 2 lc 7 title 'D-ViNE', 'SecDep_exp1.dat' using 1:3  with lines linewidth 2 lc 1 title 'SecDep0', \
'SecDep_exp3.dat' using 1:3  with lines linewidth 2 lc 2 title 'SecDep10', 'SecDep_exp4.dat' using 1:3  with lines linewidth 2 lc 3 title 'SecDep30', \
'SecDep_exp5.dat' using 1:3  with lines linewidth 2 lc 4 title 'SecDep50', 'SecDep_exp6.dat' using 1:3  with lines linewidth 2 lc 5 title 'SecDep100', \
'SecDep_exp7.dat' using 1:3  with lines linewidth 2 lc 18 title 'MixedSecDep'
set terminal postscript enhanced color "Helvetica" 8
#set terminal png
set terminal pngcairo dashed
set output "AverageNodeUtilization.png"
#set size 0.5, 0.5
set xlabel "Time"
set ylabel "Node stress ratio"
set xrange [0:50000]
set yrange [0:0.7]
set key on right bottom
plot 'DViNE.dat' using 1:4  with lines linewidth 2 lc 7 title 'D-ViNE', 'SecDep_exp1.dat' using 1:4  with lines linewidth 2 lc 1 title 'SecDep0', \
'SecDep_exp3.dat' using 1:4  with lines linewidth 2 lc 2 title 'SecDep10', 'SecDep_exp4.dat' using 1:4  with lines linewidth 2 lc 3 title 'SecDep30', \
'SecDep_exp5.dat' using 1:4  with lines linewidth 2 lc 4 title 'SecDep50', 'SecDep_exp6.dat' using 1:4  with lines linewidth 2 lc 5 title 'SecDep100', \
'SecDep_exp7.dat' using 1:4  with lines linewidth 2 lc 18 title 'MixedSecDep'
set terminal postscript enhanced color "Helvetica" 8
#set terminal png
set terminal pngcairo dashed
set output "AverageLinkUtilization.png"
#set size 0.5, 0.5
set xlabel "Time"
set ylabel "Link stress ratio"
set key on right top
set yrange [0:0.5]
set xrange [0:50000]
plot 'DViNE.dat' using 1:5  with lines linewidth 2 lc 7 title 'D-ViNE', 'SecDep_exp1.dat' using 1:5  with lines linewidth 2 lc 1  title 'SecDep0', \
'SecDep_exp3.dat' using 1:5  with lines linewidth 2 lc 2 title 'SecDep10', 'SecDep_exp4.dat' using 1:5  with lines linewidth 2 lc 3 title 'SecDep30', \
'SecDep_exp5.dat' using 1:5  with lines linewidth 2 lc 4 title 'SecDep50', 'SecDep_exp6.dat' using 1:5  with lines linewidth 2 lc 5 title 'SecDep100', \
'SecDep_exp7.dat' using 1:5  with lines linewidth 2 lc 18 title 'MixedSecDep'
#'SecDep_exp2.dat' using 1:5  with lines linewidth 2 title 'SecDep_exp2', \
