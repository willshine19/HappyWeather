2016-05-04
加入了下拉更新的功能，但是有待完善：背景图片大小的适配，更近逻辑没有加进listener

2016-05-07
修复了启动app后加载的第一个天气无法显示的问题和更新键不能工作的问题。完善了下拉更新，修正北京图片适配的问题。
目前下拉刷新没有真的刷新，只是加了500毫秒延迟。

2016-05-09
加入了ViewPager，可以左右滑动。目前ViewPager在外，下拉刷新在内
但是bug很多：不能加天气，第三个天气header位置不对，城市名不能正常显示

2016-05-11
修复了城市名显示的问题