examplars= [
    {   "currentDate":"2020-12-01",
        "tableName":"内容库产品",
        "fieldsList":"""["部门", "模块", "用户名", "访问次数", "访问人数", "访问时长", "数据日期"]""",
        "question":"比较jackjchen和robinlee在内容库的访问次数",
        "priorSchemaLinks":"""['jackjchen'->用户名, 'robinlee'->用户名]""",
        "analysis": """让我们一步一步地思考。在问题“比较jackjchen和robinlee在内容库的访问次数“中，我们被问：
“比较jackjchen和robinlee”，所以我们需要column=[用户名],cell values = ['jackjchen', 'robinlee'],所以有[用户名:('jackjchen', 'robinlee')]
”内容库的访问次数“，所以我们需要column=[访问次数]""",
        "schemaLinks":"""["用户名":("'jackjchen'", "'robinlee'"), "访问次数"]""",
        "sql":"""select 用户名, 访问次数 from 内容库产品 where 用户名 in ('jackjchen', 'robinlee')"""
        },
    {   "currentDate":"2022-11-06",
        "tableName":"内容库产品",
        "fieldsList":"""["部门", "模块", "用户名", "访问次数", "访问人数", "访问时长", "数据日期"]""",
        "question":"内容库近12个月访问人数 按部门",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库近12个月访问人数 按部门“中，我们被问：
”内容库近12个月“，所以我们需要column=[数据日期],cell values = [12],所以有[数据日期:(12)]
“访问人数”，所以我们需要column=[访问人数]
”按部门“，所以我们需要column=[部门]""",
        "schemaLinks":"""["数据日期":(12), "访问人数", "部门"]""",
        "sql":"""select 部门, 数据日期, 访问人数 from 内容库产品 where datediff('month', 数据日期, '2022-11-06') <= 12 """
        },
    {   "currentDate":"2023-04-21",
        "tableName":"内容库产品",
        "fieldsList":"""["部门", "模块", "用户名", "访问次数", "访问人数", "访问时长", "数据日期"]""",
        "question":"内容库美术部、技术研发部的访问时长",
        "priorSchemaLinks":"""['美术部'->部门, '技术研发部'->部门]""",
        "analysis": """让我们一步一步地思考。在问题“内容库美术部、技术研发部的访问时长“中，我们被问：
“访问时长”，所以我们需要column=[访问时长]
”内容库美术部、技术研发部“，所以我们需要column=[部门], cell values = ['美术部', '技术研发部'],所以有[部门:('美术部', '技术研发部')]""",
        "schemaLinks":"""["访问时长", "部门":("'美术部'", "'技术研发部'")]""",
        "sql":"""select 部门, 访问时长 from 内容库产品 where 部门 in ('美术部', '技术研发部')"""
        },
    {   "currentDate":"2023-08-21",
        "tableName":"严选",
        "fieldsList":"""["严选版权归属系", "付费模式", "结算播放份额", "付费用户结算播放份额", "数据日期"]""",
        "question":"近3天海田飞系MPPM结算播放份额",
        "priorSchemaLinks":"""['海田飞系'->严选版权归属系]""",
        "analysis": """让我们一步一步地思考。在问题“近3天海田飞系MPPM结算播放份额“中，我们被问：
“MPPM结算播放份额”，所以我们需要column=[结算播放份额], 
”海田飞系“，所以我们需要column=[严选版权归属系], cell values = ['海田飞系'],所以有[严选版权归属系:('海田飞系')],
”近3天“，所以我们需要column=[数据日期], cell values = [3],所以有[数据日期:(3)]""",
        "schemaLinks":"""["结算播放份额", "严选版权归属系":("'海田飞系'"), "数据日期":(3)]""",
        "sql":"""select 严选版权归属系, 结算播放份额 from 严选 where 严选版权归属系 = '海田飞系' and datediff('day', 数据日期, '2023-08-21') <= 3 """
        },
    {   "currentDate":"2023-05-22",
        "tableName":"歌曲库",
        "fieldsList":"""["是否潮流人歌曲", "C音歌曲ID", "C音歌曲MID", "歌曲名", "歌曲版本", "语种", "歌曲类型", "翻唱类型", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "结算播放量", "运营播放量", "付费用户结算播放量", "历史累计结算播放量", "运营搜播量", "结算搜播量", "运营完播量", "运营推播量", "近7日复播率", "日均搜播量", "数据日期"]""",
        "question":"对比近7天翻唱版和纯音乐的歌曲播放量",
        "priorSchemaLinks":"""['纯音乐'->语种, '翻唱版'->歌曲版本]""",
        "analysis": """让我们一步一步地思考。在问题“对比近3天翻唱版和纯音乐的歌曲播放量“中，我们被问：
“歌曲播放量”，所以我们需要column=[结算播放量]
”翻唱版“，所以我们需要column=[歌曲版本], cell values = ['翻唱版'],所以有[歌曲版本:('翻唱版')]
”和纯音乐的歌曲“，所以我们需要column=[语种], cell values = ['纯音乐'],所以有[语种:('纯音乐')]
”近7天“，所以我们需要column=[数据日期], cell values = [7],所以有[数据日期:(7)]""",
        "schemaLinks":"""["结算播放量", "歌曲版本":("'翻唱版'"), "语种":("'纯音乐'"), "数据日期":(7)]""",
        "sql":"""select 歌曲版本, 语种, 结算播放量 from 歌曲库 where 歌曲版本 = '翻唱版' and 语种 = '纯音乐' and datediff('day', 数据日期, '2023-05-22') <= 7 """
        },
    {   "currentDate":"2023-05-31",
        "tableName":"艺人库",
        "fieldsList":"""["上下架状态", "歌手名", "歌手等级", "歌手类型", "歌手来源", "MPPM潮流人等级", "活跃区域", "年龄", "歌手才能", "歌手风格", "粉丝数", "潮音粉丝数", "超声波粉丝数", "推博粉丝数", "超声波歌曲数", "在架歌曲数", "超声波分享数", "独占歌曲数", "超声波在架歌曲评论数", "有播放量歌曲数", "数据日期"]""",
        "question":"对比一下陈拙悬、孟梅琦、赖媚韵的粉丝数",
        "priorSchemaLinks":"""['1527896'->MPPM歌手ID, '1565463'->MPPM歌手ID, '2141459'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“对比一下陈拙悬、孟梅琦、赖媚韵的粉丝数“中，我们被问：
“粉丝数”，所以我们需要column=[粉丝数]
”陈拙悬、孟梅琦、赖媚韵“，所以我们需要column=[歌手名], cell values = ['陈拙悬', '孟梅琦', '赖媚韵'],所以有[歌手名:('陈拙悬', '孟梅琦', '赖媚韵')]""",
        "schemaLinks":"""["粉丝数", "歌手名":("'陈拙悬'", "'孟梅琦'", "'赖媚韵'")]""",
        "sql":"""select 歌手名, 粉丝数 from 艺人库 where 歌手名 in ('陈拙悬', '孟梅琦', '赖媚韵')"""
        },
    {   "currentDate":"2023-07-31",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌曲类型", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"播放量大于1万的歌曲有多少",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“播放量大于1万的歌曲有多少“中，我们被问：
“歌曲有多少”，所以我们需要column=[歌曲名]
”播放量大于1万的“，所以我们需要column=[结算播放量], cell values = [10000],所以有[结算播放量:(10000)]""",
        "schemaLinks":"""["歌曲名", "结算播放量":(10000)]""",
        "sql":"""select 歌曲名 from 歌曲库 where 结算播放量 > 10000"""
        },
    {   "currentDate":"2023-07-31",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库访问时长小于1小时，且来自美术部的用户是哪些",
        "priorSchemaLinks":"""['美术部'->部门]""",
        "analysis": """让我们一步一步地思考。在问题“内容库访问时长小于1小时，且来自美术部的用户是哪些“中，我们被问：
“用户是哪些”，所以我们需要column=[用户名]
”美术部的“，所以我们需要column=[部门], cell values = ['美术部'],所以有[部门:('美术部')]
”访问时长小于1小时“，所以我们需要column=[访问时长], cell values = [1],所以有[访问时长:(1)]""",
        "schemaLinks":"""["用户名", "部门":("'美术部'"), "访问时长":(1)]""",
        "sql":"""select 用户名 from 内容库产品 where 部门 = '美术部' and 访问时长 < 1"""
        },
    {   "currentDate":"2023-08-31",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库pv最高的用户有哪些",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库pv最高的用户有哪些“中，我们被问：
“用户有哪些”，所以我们需要column=[用户名]
”pv最高的“，所以我们需要column=[访问次数], cell values = [1],所以有[访问次数:(1)]""",
        "schemaLinks":"""["用户名", "访问次数":(1)]""",
        "sql":"""select 用户名 from 内容库产品 order by 访问次数 desc limit 1"""
        },
    {   "currentDate":"2023-08-31",
        "tableName":"艺人库",
        "fieldsList":"""["播放量层级", "播放量单调性", "播放量方差", "播放量突增类型", "播放量集中度", "歌手名", "歌手等级", "歌手类型", "歌手来源", "MPPM潮流人等级", "结算播放量", "运营播放量", "历史累计结算播放量", "有播放量歌曲数", "历史累计运营播放量", "付费用户结算播放量", "结算播放量占比", "运营播放份额", "免费用户结算播放占比", "完播量", "数据日期"]""",
        "question":"近90天袁亚伟播放量平均值是多少",
        "priorSchemaLinks":"""['152789226'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“近90天袁亚伟播放量平均值是多少“中，我们被问：
“播放量平均值是多少”，所以我们需要column=[结算播放量]
”袁亚伟“，所以我们需要column=[歌手名], cell values = ['袁亚伟'],所以有[歌手名:('袁亚伟')]
”近90天“，所以我们需要column=[数据日期], cell values = [90],所以有[数据日期:(90)]""",
        "schemaLinks":"""["结算播放量", "歌手名":("'袁亚伟'"), "数据日期":(90)]""",
        "sql":"""select avg(结算播放量) from 艺人库 where 歌手名 = '袁亚伟' and datediff('day', 数据日期, '2023-08-31') <= 90 """
        },
    {   "currentDate":"2023-08-31",
        "tableName":"艺人库",
        "fieldsList":"""["播放量层级", "播放量单调性", "播放量方差", "播放量突增类型", "播放量集中度", "歌手名", "歌手等级", "歌手类型", "歌手来源", "MPPM潮流人等级", "结算播放量", "运营播放量", "历史累计结算播放量", "有播放量歌曲数", "历史累计运营播放量", "付费用户结算播放量", "结算播放量占比", "运营播放份额", "免费用户结算播放占比", "完播量", "数据日期"]""",
        "question":"周倩倩近7天结算播放量总和是多少",
        "priorSchemaLinks":"""['199509'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“周倩倩近7天结算播放量总和是多少“中，我们被问：
“结算播放量总和是多少”，所以我们需要column=[结算播放量]
”周倩倩“，所以我们需要column=[歌手名], cell values = ['周倩倩'],所以有[歌手名:('周倩倩')]
”近7天“，所以我们需要column=[数据日期], cell values = [7],所以有[数据日期:(7)]""",
        "schemaLinks":"""["结算播放量", "歌手名":("'周倩倩'"), "数据日期":(7)]""",
        "sql":"""select sum(结算播放量) from 艺人库 where 歌手名 = '周倩倩' and datediff('day', 数据日期, '2023-08-31') <= 7 """
        },
    {   "currentDate":"2023-09-14",
        "tableName":"内容库产品",
        "fieldsList":"""["部门", "模块", "用户名", "访问次数", "访问人数", "访问时长", "数据日期"]""",
        "question":"内容库访问次数大于1k的部门是哪些",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库访问次数大于1k的部门是哪些“中，我们被问：
“部门是哪些”，所以我们需要column=[部门]
”访问次数大于1k的“，所以我们需要column=[访问次数], cell values = [1000],所以有[访问次数:(1000)]""",
        "schemaLinks":"""["部门", "访问次数":(1000)]""",
        "sql":"""select 部门 from 内容库产品 where 访问次数 > 1000"""
        },
    {   "currentDate":"2023-09-18",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "MPPM歌手ID", "歌曲版本", "歌曲类型", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"陈亿训唱的所有的播放量大于20k的孤勇者有哪些",
        "priorSchemaLinks":"""['199509'->MPPM歌手ID, '1527123'->MPPM歌曲ID]""",
        "analysis": """让我们一步一步地思考。在问题“陈亿训唱的所有的播放量大于20k的孤勇者有哪些“中，我们被问：
“孤勇者有哪些”，所以我们需要column=[歌曲名], cell values = ['孤勇者'],所以有[歌曲名:('孤勇者')]
”播放量大于20k的“，所以我们需要column=[结算播放量], cell values = [20000],所以有[结算播放量:(20000)]
”陈亿训唱的“，所以我们需要column=[歌手名], cell values = ['陈亿训'],所以有[歌手名:('陈亿训')]""",
        "schemaLinks":"""["歌曲名":("'孤勇者'"), "结算播放量":(20000), "歌手名":("'陈亿训'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where 结算播放量 > 20000 and 歌手名 = '陈亿训' and 歌曲名 = '孤勇者'"""
        },
    {   "currentDate":"2023-09-18",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"周洁轮去年发布的歌曲有哪些",
        "priorSchemaLinks":"""['23109'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“周洁轮去年发布的歌曲有哪些“中，我们被问：
“歌曲有哪些”，所以我们需要column=[歌曲名]
”去年发布的“，所以我们需要column=[发布时间], cell values = [1],所以有[发布时间:(1)]
”周洁轮“，所以我们需要column=[歌手名], cell values = ['周洁轮'],所以有[歌手名:('周洁轮')]""",
        "schemaLinks":"""["歌曲名", "发布时间":(1), "歌手名":("'周洁轮'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where datediff('year', 发布时间, '2023-09-18') <= 1 and 歌手名 = '周洁轮'"""
        },
    {   "currentDate":"2023-09-11",
        "tableName":"艺人库",
        "fieldsList":"""["播放量层级", "播放量单调性", "播放量方差", "播放量突增类型", "播放量集中度", "歌手名", "歌手等级", "歌手类型", "歌手来源", "签约日期", "MPPM潮流人等级", "结算播放量", "运营播放量", "历史累计结算播放量", "有播放量歌曲数", "历史累计运营播放量", "付费用户结算播放量", "结算播放量占比", "运营播放份额", "免费用户结算播放占比", "完播量", "数据日期"]""",
        "question":"我想要近半年签约的播放量前十的歌手有哪些",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“我想要近半年签约的播放量前十的歌手“中，我们被问：
“歌手有哪些”，所以我们需要column=[歌手名]
”播放量前十的“，所以我们需要column=[结算播放量], cell values = [10],所以有[结算播放量:(10)]
”近半年签约的“，所以我们需要column=[签约日期], cell values = [0.5],所以有[签约日期:(0.5)]""",
        "schemaLinks":"""["歌手名", "结算播放量":(10), "签约日期":(0.5)]""",
        "sql":"""select 歌手名 from 艺人库 where datediff('year', 签约日期, '2023-09-11') <= 0.5 order by 结算播放量 desc limit 10"""
        },
    {   "currentDate":"2023-08-12",
        "tableName":"歌曲库",
        "fieldsList": """["发行日期", "歌曲语言", "歌曲来源", "歌曲流派", "歌曲名", "歌曲版本", "歌曲类型", "发行时间", "数据日期"]""",
        "question":"最近一年发行的歌曲中，有哪些在近7天播放超过一千万的",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“最近一年发行的歌曲中，有哪些在近7天播放超过一千万的“中，我们被问：
“发行的歌曲中，有哪些”，所以我们需要column=[歌曲名]
”最近一年发行的“，所以我们需要column=[发行日期], cell values = [1],所以有[发行日期:(1)]
”在近7天播放超过一千万的“，所以我们需要column=[数据日期, 结算播放量], cell values = [7, 10000000],所以有[数据日期:(7), 结算播放量:(10000000)]""",
        "schemaLinks":"""["歌曲名", "发行日期":(1), "数据日期":(7), "结算播放量":(10000000)]""",
        "sql":"""select 歌曲名 from 歌曲库 where datediff('year', 发行日期, '2023-08-12') <= 1 and datediff('day', 数据日期, '2023-08-12') <= 7 and 结算播放量 > 10000000"""
        },
    {   "currentDate":"2023-08-12",
        "tableName":"歌曲库",
        "fieldsList": """["发行日期", "歌曲语言", "歌曲来源", "歌曲流派", "歌曲名", "歌曲版本", "歌曲类型", "发行时间", "数据日期"]""",
        "question":"今年以来发行的歌曲中，有哪些在近7天播放超过一千万的",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“今年以来发行的歌曲中，有哪些在近7天播放超过一千万的“中，我们被问：
“发行的歌曲中，有哪些”，所以我们需要column=[歌曲名]
”今年以来发行的“，所以我们需要column=[发行日期], cell values = [0],所以有[发行日期:(0)]
”在近7天播放超过一千万的“，所以我们需要column=[数据日期, 结算播放量], cell values = [7, 10000000],所以有[数据日期:(7), 结算播放量:(10000000)]""",
        "schemaLinks":"""["歌曲名", "发行日期":(0), "数据日期":(7), "结算播放量":(10000000)]""",
        "sql":"""select 歌曲名 from 歌曲库 where datediff('year', 发行日期, '2023-08-12') <= 0 and datediff('day', 数据日期, '2023-08-12') <= 7 and 结算播放量 > 10000000"""
        },
    {   "currentDate":"2023-08-12",
        "tableName":"歌曲库",
        "fieldsList": """["发行日期", "歌曲语言", "歌曲来源", "歌曲流派", "歌曲名", "歌曲版本", "歌曲类型", "发行时间", "数据日期"]""",
        "question":"2023年以来发行的歌曲中，有哪些在近7天播放超过一千万的",
        "priorSchemaLinks":"""['514129144'->MPPM歌曲ID]""",
        "analysis": """让我们一步一步地思考。在问题“2023年以来发行的歌曲中，有哪些在近7天播放超过一千万的“中，我们被问：
“发行的歌曲中，有哪些”，所以我们需要column=[歌曲名]
”2023年以来发行的“，所以我们需要column=[发行日期], cell values = ['2023-01-01'],所以有[发行日期:('2023-01-01')]
”在近7天播放超过一千万的“，所以我们需要column=[数据日期, 结算播放量], cell values = [7, 10000000],所以有[数据日期:(7), 结算播放量:(10000000)]""",
        "schemaLinks":"""["歌曲名", "发行日期":("'2023-01-01'"), "数据日期":(7), "结算播放量":(10000000)]""",
        "sql":"""select 歌曲名 from 歌曲库 where 发行日期 >= '2023-01-01' and datediff('day', 数据日期, '2023-08-12') <= 7 and 结算播放量 > 10000000"""
        },
    {   "currentDate":"2023-08-01",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"周洁轮2023年6月之后发布的歌曲有哪些",
        "priorSchemaLinks":"""['23109'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“周洁轮2023年6月之后发布的歌曲有哪些“中，我们被问：
“歌曲有哪些”，所以我们需要column=[歌曲名]
”2023年6月之后发布的“，所以我们需要column=[发布时间], cell values = ['2023-06-01'],所以有[发布时间:('2023-06-01')]
”周洁轮“，所以我们需要column=[歌手名], cell values = ['周洁轮'],所以有[歌手名:('周洁轮')]""",
        "schemaLinks":"""["歌曲名", "发布时间":("'2023-06-01'"), "歌手名":("'周洁轮'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where 发布时间 >= '2023-06-01' and 歌手名 = '周洁轮'"""
        },
    {   "currentDate":"2023-08-01",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"邓梓琦在2023年1月5日之后发布的歌曲中，有哪些播放量大于500W的？",
        "priorSchemaLinks":"""['2312311'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“邓梓琦在2023年1月5日之后发布的歌曲中，有哪些播放量大于500W的？“中，我们被问：
“歌曲中，有哪些”，所以我们需要column=[歌曲名]
“播放量大于500W的”，所以我们需要column=[结算播放量], cell values = [5000000],所以有[结算播放量:(5000000)]
”邓梓琦在2023年1月5日之后发布的“，所以我们需要column=[发布时间], cell values = ['2023-01-05'],所以有[发布时间:('2023-01-05')]
”邓梓琦“，所以我们需要column=[歌手名], cell values = ['邓梓琦'],所以有[歌手名:('邓梓琦')]""",
        "schemaLinks":"""["歌曲名", "结算播放量":(5000000), "发布时间":("'2023-01-05'"), "歌手名":("'邓梓琦'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where 发布时间 >= '2023-01-05' and 歌手名 = '邓梓琦' and 结算播放量 > 5000000"""
        },
    {   "currentDate":"2023-09-17",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"2023年6月以后，张亮英播放量大于200万的歌曲有哪些？",
        "priorSchemaLinks":"""['45453'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“2023年6月以后，张亮英播放量大于200万的歌曲有哪些？“中，我们被问：
“播放量大于200万的”，所以我们需要column=[结算播放量], cell values = [2000000],所以有[结算播放量:(2000000)]
”2023年6月以后，张亮英“，所以我们需要column=[数据日期, 歌手名], cell values = ['2023-06-01', '张亮英'],所以有[数据日期:('2023-06-01'), 歌手名:('张亮英')],
”歌曲有哪些“，所以我们需要column=[歌曲名]""",
        "schemaLinks":"""["结算播放量":(2000000), "数据日期":("'2023-06-01'"), "歌手名":("'张亮英'"), "歌曲名"]""",
        "sql":"""select 歌曲名 from 歌曲库 where 数据日期 >= '2023-06-01' and 歌手名 = '张亮英' and 结算播放量 > 2000000"""
        },
    {   "currentDate":"2023-08-16",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"2021年6月以后发布的李雨纯的播放量大于20万的歌曲有哪些",
        "priorSchemaLinks":"""['23109'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“2021年6月以后发布的李雨纯的播放量大于20万的歌曲有哪些“中，我们被问：
“播放量大于20万的”，所以我们需要column=[结算播放量], cell values = [200000],所以有[结算播放量:(200000)]
”2021年6月以后发布的“，所以我们需要column=[发布时间], cell values = ['2021-06-01'],所以有[发布时间:('2021-06-01')]
”李雨纯“，所以我们需要column=[歌手名], cell values = ['李雨纯'],所以有[歌手名:('李雨纯')]""",
        "schemaLinks":"""["结算播放量":(200000), "发布时间":("'2021-06-01'"), "歌手名":("'李雨纯'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where 发布时间 >= '2021-06-01' and 歌手名 = '李雨纯' and 结算播放量 > 200000"""
        },
    {   "currentDate":"2023-08-16",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲名", "歌曲版本", "歌手名", "歌曲类型", "发布时间", "MPPM歌曲ID", "是否严选窄口径歌曲", "是否严选宽口径歌曲", "是否潮流人歌曲", "超声波歌曲ID", "C音歌曲ID", "C音歌曲MID", "结算播放量", "运营播放量", "分享量", "收藏量", "运营搜播量", "结算搜播量", "拉新用户数", "拉活用户数", "分享率", "结算播放份额", "数据日期"]""",
        "question":"刘锝桦在1992年4月2日到2020年5月2日之间发布的播放量大于20万的歌曲有哪些",
        "priorSchemaLinks":"""['4234234'->MPPM歌手ID]""",
        "analysis": """让我们一步一步地思考。在问题“刘锝桦在1992年4月2日到2020年5月2日之间发布的播放量大于20万的歌曲有哪些“中，我们被问：
“播放量大于20万的”，所以我们需要column=[结算播放量], cell values = [200000],所以有[结算播放量:(200000)]
”1992年4月2日到2020年5月2日之间发布的“， 所以我们需要column=[发布时间], cell values = ['1992-04-02', '2020-05-02'],所以有[发布时间:('1992-04-02', '2020-05-02')]
”刘锝桦“，所以我们需要column=[歌手名], cell values = ['刘锝桦'],所以有[歌手名:('刘锝桦')]""",
        "schemaLinks":"""["结算播放量":(200000), "发布时间":("'1992-04-02'", "'2020-05-02'"), "歌手名":("'刘锝桦'")]""",
        "sql":"""select 歌曲名 from 歌曲库 where 发布时间 >= '1992-04-02' and 发布时间 <= '2020-05-02' and 歌手名 = '刘锝桦' and 结算播放量 > 200000"""
        },
    {
        "currentDate":"2023-09-04",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库近30天访问次数的平均数",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库近30天访问次数的平均数“中，我们被问：
“访问次数的平均数”，所以我们需要column=[访问次数]
”内容库近30天“，所以我们需要column=[数据日期], cell values = [30],所以有[数据日期:(30)]""",
        "schemaLinks":"""["访问次数", "数据日期":(30)]""",
        "sql":"""select avg(访问次数) from 内容库产品 where datediff('day', 数据日期, '2023-09-04') <= 30 """
        },
    {
        "currentDate":"2023-09-04",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库近半年哪个月的访问次数汇总最高",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库近半年哪个月的访问次数汇总最高“中，我们被问：
“访问次数汇总最高”，所以我们需要column=[访问次数], cell values = [1],所以有[访问次数:(1)]
”内容库近半年“，所以我们需要column=[数据日期], cell values = [0.5],所以有[数据日期:(0.5)]""",
        "schemaLinks":"""["访问次数":(1), "数据日期":(0.5)]""",
        "sql":"""select MONTH(数据日期), sum(访问次数) from 内容库产品 where datediff('year', 数据日期, '2023-09-04') <= 0.5 group by MONTH(数据日期) order by sum(访问次数) desc limit 1"""
        },
    {
        "currentDate":"2023-09-04",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库近半年每个月的平均访问次数",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库近半年每个月的平均访问次数“中，我们被问：
“每个月的平均访问次数”，所以我们需要column=[访问次数]
”内容库近半年“，所以我们需要column=[数据日期], cell values = [0.5],所以有[数据日期:(0.5)]""",
        "schemaLinks":"""["访问次数", "数据日期":(0.5)]""",
        "sql":"""select MONTH(数据日期), avg(访问次数) from 内容库产品 where datediff('year', 数据日期, '2023-09-04') <= 0.5 group by MONTH(数据日期)"""
        },
    {
        "currentDate":"2023-09-10",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"内容库 按部门统计访问次数 top10 的部门",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“内容库 按部门统计访问次数 top10 的部门“中，我们被问：
“访问次数 top10 的部门”，所以我们需要column=[访问次数], cell values = [10],所以有[访问次数:(10)]
”内容库 按部门统计“，所以我们需要column=[部门]""",
        "schemaLinks":"""["访问次数":(10), "部门"]""",
        "sql":"""select 部门, sum(访问次数) from 内容库产品 group by 部门 order by sum(访问次数) desc limit 10"""
        },
    {
        "currentDate":"2023-09-10",
        "tableName":"内容库产品",
        "fieldsList":"""["用户名", "部门", "模块", "访问时长", "访问次数", "访问人数", "数据日期"]""",
        "question":"超音速 近7个月，月度总访问量超过 2万的月份",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“超音速 近7个月，月度总访问量超过 2万的月份“中，我们被问：
“月度总访问量超过 2万的月份”，所以我们需要column=[访问次数], cell values = [20000],所以有[访问次数:(20000)]
”超音速 近7个月“，所以我们需要column=[数据日期], cell values = [7],所以有[数据日期:(7)]""",
        "schemaLinks":"""["访问次数":(20000), "数据日期":(7)]""",
        "sql":"""select MONTH(数据日期) from 内容库产品 where datediff('month', 数据日期, '2023-09-10') <= 7 group by MONTH(数据日期) having sum(访问次数) > 20000"""
        },
    {
        "currentDate":"2023-09-10",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲语言", "歌曲来源", "运营播放量", "播放量", "歌曲名", "结算播放量", "专辑名", "发布日期", "歌曲版本", "歌曲类型", "数据日期"]""",
        "question":"2022年7月到2023年7月之间发布到歌曲，按播放量取top 100，再按月粒度来统计近1年的运营播放量",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“2022年7月到2023年7月之间发布到歌曲，按播放量取top 100，再按月粒度来统计近1年的运营播放量“中，我们被问：
“按月粒度来统计近1年的运营播放量”，所以我们需要column=[运营播放量, 数据日期], cell values = [1],所以有[运营播放量, 数据日期:(1)]
”按播放量取top 100“，所以我们需要column=[播放量], cell values = [100],所以有[播放量:(100)]
“2022年7月到2023年7月之间发布到歌曲”，所以我们需要column=[发布日期], cell values = ['2022-07-01', '2023-07-01'],所以有[发布日期:('2022-07-01', '2023-07-01')]""",
        "schemaLinks":"""["运营播放量", "数据日期":(1), "播放量":(100), "发布日期":("'2022-07-01'", "'2023-07-01'")]""",
        "sql":"""select MONTH(数据日期), sum(运营播放量) from (select 数据日期, 运营播放量 from 歌曲库 where 发布日期 >= '2022-07-01' and 发布日期 <= '2023-07-01' order by 播放量 desc limit 100) t where datediff('year', 数据日期, '2023-09-10') <= 1 group by MONTH(数据日期)"""
        },
    {
        "currentDate":"2023-09-10",
        "tableName":"歌曲库",
        "fieldsList":"""["歌曲语言", "歌曲来源", "运营播放量", "播放量", "歌曲名", "结算播放量", "专辑名", "发布日期", "歌曲版本", "歌曲类型", "数据日期"]""",
        "question":"2022年7月到2023年7月之间发布到歌曲，按播放量取top100，再按月粒度来统计近1年的运营播放量之和，筛选出其中运营播放量之和大于2k的月份",
        "priorSchemaLinks":"""[]""",
        "analysis": """让我们一步一步地思考。在问题“2022年7月到2023年7月之间发布到歌曲，按播放量取top100，再按月粒度来统计近1年的运营播放量之和，筛选出其中运营播放量之和大于2k的月份“中，我们被问：
“筛选出其中运营播放量之和大于2k的月份”，所以我们需要column=[运营播放量], cell values = [2000],所以有[运营播放量:(2000)]
”按月粒度来统计近1年的运营播放量之和“，所以我们需要column=[数据日期], cell values = [1],所以有[数据日期:(1)]
”按播放量取top100“，所以我们需要column=[播放量], cell values = [100],所以有[播放量:(100)]
”2022年7月到2023年7月之间发布到歌曲“，所以我们需要column=[发布日期], cell values = ['2022-07-01', '2023-07-01'],所以有[发布日期:('2022-07-01', '2023-07-01')]""",
        "schemaLinks":"""["运营播放量":(2000), "数据日期":(1), "播放量":(100), "发布日期":("'2022-07-01'", "'2023-07-01'")]""",
        "sql":"""select MONTH(数据日期), sum(运营播放量) from (select 数据日期, 运营播放量 from 歌曲库 where 发布日期 >= '2022-07-01' and 发布日期 <= '2023-07-01' order by 播放量 desc limit 100) t where datediff('year', 数据日期, '2023-09-10') <= 1 group by MONTH(数据日期) having sum(运营播放量) > 2000"""
    },
    {
        "currentDate":"2023-11-01",
        "tableName":"营销月模型",
        "fieldsList":"""["国家中文名", "机型类别", "销量", "数据日期"]""",
        "question":"今年智能机在哪个国家的销量之和最高",
        "priorSchemaLinks":"""['智能机'->机型类别]""",
        "analysis": """让我们一步一步地思考。在问题“今年智能机在哪个国家的销量之和最高“中，我们被问：
“销量最高”，所以我们需要column=[销量], cell values = [1],所以有[销量:(1)]
”今年“，所以我们需要column=[数据日期], cell values = ['2023-01-01', '2023-11-01'],所以有[数据日期:('2023-01-01', '2023-11-01')]
”智能机“，所以我们需要column=[机型类别], cell values = ['智能机'],所以有[机型类别:('智能机')]""",
        "schemaLinks":"""["销量":(1), "数据日期":("'2023-01-01'", "'2023-11-01'"), "机型类别":("'智能机'")]""",
        "sql":"""select 国家中文名, sum(销量) from 营销月模型 where 机型类别 = '智能机' and 数据日期 >= '2023-01-01' and 数据日期 <= '2023-11-01' group by 国家中文名 order by sum(销量) desc limit 1"""
    }
]