#!/bin/bash

foreach i (0 1 2)
   ../../../bin/edriver r10-$i -nd -hh -ll -hl
end
