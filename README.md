2016-05-04
加入了下拉更新的功能，但是有待完善：背景图片大小的适配，更近逻辑没有加进listener

2016-05-07
修复了启动app后加载的第一个天气无法显示的问题和更新键不能工作的问题。完善了下拉更新，修正北京图片适配的问题。
目前下拉刷新没有真的刷新，只是加了500毫秒延迟。

2016-05-09
加入了ViewPager，可以左右滑动。目前ViewPager在外，下拉刷新在内
但是bug很多：不能加天气，第三个天气header位置不对，城市名不能正常显示

2016-05-11
修复了城市名显示的问题，更换了PagerAdapter，用Fragment代替View。
问题：header的位置问题依然没有解决。

2016-05-13
加入volly gson

2016-05-14
无法解决header的位置问题。准备新建一个分支conflict，尝试另一种方法，解决滑动冲突
成功解决header的问题：下拉刷新在外，viewpager在内。解决滑动冲突。修复前台服务。

2016-05-15
加号按钮

2016-05-16
实现添加天气的功能,DrawerLayout侧滑菜单,可关注城市天气
加了toolbar，但是setSupportActionBar方法报错，不能加加天气的按钮

2016-05-17
去掉toolbar。提供鲁棒性，网络异常也可以正常显示。添加了删除天气的功能

待完善
 RecyclerView

2016 8 21
准备换知心天气的api，加入Retrofit，下一步重新组织数据
问题：下载天气如果更新到ui比较好

8 22
换了知心天气，可以正常选择城市，删除城市，切换城市，显示天气，保存已关注的天气。重构了WeatherActivity，是数据保存更合理
todo 下拉刷新
