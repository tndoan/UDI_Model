# UDI Model

This is my implementation for [UDI model](http://web.engr.illinois.edu/~ruili1/udi-kdd12-ruili-aug12.pdf).

The dataset could be download from [UIUC website](https://wiki.cites.illinois.edu/wiki/display/forward/Dataset-UDI-TwitterCrawl-Aug2012).

*Note:* After finishing my implementation, I have found [another implementation](https://github.com/networkdynamics/geoinference/tree/master/python/src/geolocate/gimethods/user-profiling) for this paper using Python. One main difference between my implementation and theirs is the stop condition for loop. For the outer loop, their algorithm stops when the home locations are stable i.e. not change much. My implementation stops when loglikelihood of model is stable.
