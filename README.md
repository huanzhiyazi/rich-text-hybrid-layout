rich-text-hybrid-layout
=======================

这是一个Android 客户端富文本排列程序，其主要功能是：

输入一段格式化的文本，其中包含有如下富文本类型：纯文本、图片、超链接、表情、纯 URL。解析里面的所有富文本，并以合适的形式在
Android 客户端进行显示。

1. hybarrange/ 文件夹下是源代码程序；
2. appcompat_v7/ 文件夹下是 app compat v7 扩展包，兼容低版本所用；
3. apk/ 下是源代码程序编译的 apk 文件，供安装试用。

核心功能：
主要集中在在源代码的 com.example.hybarrangedemo.utils 包中，这是一个用装饰模式封装的富文本解析组件，如果你需要添加新的富文本
类型，你可以实现一个 IParser ，但是要注意不同富文本之间正则表达式的包含关系，避免重复解析，具体请参考 com.example.hybarrangedemo.utils.WeburlParser 的实现。

关于图片加载：
图片的异步加载使用了@nostra13 大神的 Android-Universal-Image-Loader 组件https://github.com/nostra13/Android-Universal-Image-Loader
这是一个强大到令人发指的图片加载神器，它几乎包括了所有你能想象到的所有图片加载的功能。

关于缺点：
1. 其正则规律具有包含关系的不同富文本的处理缺乏可扩展性，目前只对 Web url ，图片，超链接这三种特定的富文本进行了过滤处理，以
避免重复解析；
2. 请狠戳你能发现的一切不爽的地方并通知我！
