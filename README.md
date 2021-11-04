rich-text-hybrid-layout
=======================

这是一个Android 客户端富文本排列程序，其主要功能是：

输入一段格式化的文本，其中包含有如下富文本类型：纯文本、图片、超链接、表情、纯 URL。解析里面的所有富文本，并以合适的形式在
Android 客户端进行显示。

<p><b>核心功能：</b></p>
主要集中在在源代码的 com.example.hybarrangedemo.utils 包中，这是一个用装饰模式封装的富文本解析组件，如果你需要添加新的富文本
类型，你可以实现一个 IParser ，但是要注意不同富文本之间正则表达式的包含关系，避免重复解析，具体请参考 com.example.hybarrangedemo.utils.WeburlParser 的实现。

<p><b>富文本解析器的使用方式：</b></p>

		// 通过迭代装饰方式构造解析器。
		IParser parser = new SmileyParser(mContext);
		parser = new ImageParser(mContext, parser);
		parser = new HyperlinkParser(mContext, parser);
		parser = new WeburlParser(mContext, parser);
		
		// 执行解析并返回解析文本段队列。
		ParseManager manager = new ParseManager();
		ArrayList<ParsedSegment> segments = manager.parse(parser, rich);
		
其中 rich 为需要解析的格式化的输入文本， ParsedSegment 中包含了解析和用 span 修饰过的文本段，有两个字段：

1. text 解析和用 span 修饰过的文本；
2. 文本类型：未知类型（纯文本或者非图片富文本）或图片。


<p><b>关于缺点：</b></p>
其正则规律具有包含关系的不同富文本的处理缺乏可扩展性，目前只对 Web url ，图片，超链接这三种特定的富文本进行了过滤处理，以
避免重复解析。
