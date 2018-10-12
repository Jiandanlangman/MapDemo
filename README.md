# MapDemo
一个轻量，高性能，扩展性强的地图框架   
gif图较大，浏览器加载比较缓慢，等待加载结束或下载源码手动运行查看效果  
这只是一个Demo，并不是源码，但源码已经封装成aar，可参照Demo中的接入方式接入，如果最源码有兴趣，可以联系我  
### 注意
- 上层代码是用kotlin编写的，所以你的项目必须引入kotlin
- AndroidManifest.xml中必须开启largeHeap属性和hardwareAccelerated属性，否则可能会出现性能降低或内存溢出
- 性能经过反复测试，一般的手机绘制一帧地图只需2-5ms，就算几年前的老人机绘制一帧也只需要10-15ms，远远高于每秒60帧，所以流畅度完全没问题
- 必须加载.map格式的地图资源包，地图制作工具已经开源，项目地址在：https://github.com/Jiandanlangman/MapGenerator
![ScreenRecord](https://github.com/Jiandanlangman/MapDemo/blob/master/screenrecord.gif)
