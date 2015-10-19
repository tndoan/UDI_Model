# UDI Model

This is my implementation for [UDI model](http://web.engr.illinois.edu/~ruili1/udi-kdd12-ruili-aug12.pdf).

The dataset could be download from [UIUC website](https://wiki.cites.illinois.edu/wiki/display/forward/Dataset-UDI-TwitterCrawl-Aug2012).

*Note:* After finishing my implementation, I have found [another implementation](https://github.com/networkdynamics/geoinference/tree/master/python/src/geolocate/gimethods/user-profiling) for this paper using Python. One main difference between my implementation and theirs is the stop condition for loop. For the outer loop, their algorithm stops when the home locations are stable i.e. not change much. My implementation stops when loglikelihood of model is stable.

If a user follows only 1 user or he/she tweets only one venue, the division by zero bugs will happen. Some ways to avoid this case
- Auto assign these users by venue that s/he tweet or home location of his following.
- Ignore this kind of user because they do not contribute much information for the model.
