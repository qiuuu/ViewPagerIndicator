# ViewPagerIndicator
custom view
创建自定义视图的两种方法：
1. 扩展已存在的类
2.  扩展view类／viewGroup类

# 视图自定义扩展：
最简单的方法是扩展现有的视图类。可以说，从视图中获取一些自定义逻辑或自定义行为是最简单的方法。
# 视图平面自定义：
如果你真的很冒险，你可以做平面定制视图。而不是扩展更高级别的小部件，而是扩展基本的U类。通常，当您想要完全自定义绘图逻辑时，您将执行此操作，如果您有自己想要做的所有行为都很酷。那就是使用平面自定义组。平面定制组也对性能有好处。如果您的布局有很多内容，而不是使用一堆不同的视图来实现该UI或该行为，则可以使用单个视图来执行相同的操作。通过这种方式，您可以减少视图树中的级别数，并减少正在使用的视图数。

三种测量模式：
* EXACTLY：表示设置了精确的值，一般当childView设置其宽、高为精确值、match_parent时，ViewGroup会将其设置为EXACTLY；
* AT_MOST：表示子布局被限制在一个最大值内，一般当childView设置其宽、高为wrap_content时，ViewGroup会将其设置为AT_MOST；
* UNSPECIFIED：表示子布局想要多大就多大，一般出现在AadapterView的item的heightMode中、ScrollView的childView的heightMode中；此种模式比较少见。

(http://ndquangr.blogspot.com/2013/04/android-view-lifecycle.html)
