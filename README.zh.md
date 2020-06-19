# Tars JMeter

Tars JMeter是一款针对Tars协议进行私有化定制的JMeter测试插件，其目的是为了帮助用户解决Tars服务的性能评估与测试。您可能会在以下场景使用到Tars JMeter:

1. 您开发了一组Tars服务，需要对这组RPC服务，进行简单自测时，您可以使用JMeter搭载该插件，基于Tup 收发进行无需编码的接口单测；
2. 您需要对您的Tars接口进行性能评估、压力测试、稳定性测试时，您可以使用JMeter搭载该插件。

TarsJMeter特点如下：

1. 易用性强，用户只需对JMeter有一定的了解，即可采用TarsJMeter简洁的UI实现测试用例开发。
2. 支持分布式，通过JMeter的集群模式，可实现Tars服务的负载测试。目前我们有使用集群模式轻松触顶测试过吞吐量达50000TPS的Tars单服务。
3. 可测复杂场景，TarsJMeter结合JMeter丰富的 Logic Controller, Pre Processors, Post Processors,Timer,Config Element等组件，可丰富测试场景，使得测试用例不再是单一的接口测试。
4. 数据可监控，JMeter可把Tars服务的测试数据上报至InfluxDB（时序数据库），InfluxDB可与第三方监控平台对接，实现对数据流量的实时监控。

## 安装步骤

1. 安装**JAVA JDK** (建议java8以上)；
2. [下载JMeter](https://jmeter.apache.org/download_jmeter.cgi)，解压到本地目录即可，建议JMeter 5.2及以上版本；
3. 安装目录下的/bin 存放了jmeter可执行文件; **安装目录下的/lib/ext**可以添加扩展的第三方协议测试库；
4. Gradle编译打包或者[dist下载](https://github.com/TarsCloud/TarsJMeter/blob/master/dist/tars-jmeter-1.7.1.jar)
5. 生成或下载tars_jmeter.jar后，把它放入JMeter安装目录下的**/lib/ext**里。

## 用例编写步骤

1. 打开JMeter（执行JMeter目录下的bin/jmeter.bat或bin/jmeter.sh）

2. 依次添加线程组、取样器（Sampler），出现通用Tars请求表示安装成功。
<img src="./res/step1.png" width="600" height="400" align=center/>

3. 选择通用Tars请求，创建采样器完成。
    tars服务文件示例：
  ```
  module TestApp
  {
  	struct User {
  	    0 require string name;
  	};
  
  	interface Hello
  	{
  	    string hello(int no, string name);
  	    string hello2(string name);
  	    int hello3(int no, string name, out string meg);
  	    int hello4(int no, out User user);
  	};
  };
  ```

  采样器示例配置：

  <img src="./res/step2.png" width="600" height="400" align=center/>

  * 被测服务地址：待测Tars服务的IP

  * 被测服务端口：待测Tars服务端口

  * 被测服务路径：待测Tars服务的Servant节点信息，例如：TestTars.HelloServer.HelloObj

  * 被测接口方法：待测Tars服务的被测函数方法，例如：hello4

  * 接口返回值类型：被测函数方法的Return返回值

  * tars2json：本地tars文件或包含tars的目录中tars结构体自动转换为供测试使用的json格式

    <img src="./res/step7.png" width="600" height="400" align=center/>

  * 方法参数列表：名称（自定义，一般为函数的入参变量名），方法参数列表（根据函数的入参变量类型，转换为对应的json格式），type（选择函数的入参类型）例如：int hello4(int no, out User user);

  * Tars扩展参数：提供基本的Tars测试环境配置，可修改

    <img src="./res/step3.png" width="600" height="400"/>

  * 请求上下文：客户context（上下文信息）上报，客户端至Tars服务端单向。

    <img src="./res/step4.png" width="600" height="400"/>

  * 请求状态：客户与Tars服务间双向交互状态信息

    <img src="./res/step5.png" width="600" height="400"/>

    

4. 点击运行，查看树结果，可获取Tars服务的响应信息<img src="./res/step6.png" width="600" height="400"/>

## Tars Json数据定义及使用

插件使用`json`来定义目标Tars方法的入参或返回值数据，这样可以做到Tars结构化数据能够更好的可视化。插件集成的`Tars2JsonMojo`工具，提供从Tars Struct到 Json数据的快速转换能力。json可视化数据支持类型列表如下：

`tars`  //tars结构化数据
`map`   //map数据
`vector`    //复杂数据数组
`boolean`   // 8种基本类型数据
`byte`
`int`
`short`
`long`
`float`
`double`
`string`
`boolean[]` // 8种基本类型数据的数组
`byte[]`
`int[]`
`short[]`
`long[]`
`float[]`
`double[]`
`string[]`

### 数据定义举例

基础类型及基础类型数组的定义：
- string
    ```json
    {
      "type" : "string",
      "value" : "this is a primitive type"
    }
    ```
    
- int

    ```json
    {
      "type" : "int",
      "value" : 100
    }
    ```

- int[]
  ```json
  {
    "type" : "int[]",
    "value" : [100 , 200 ]
  }
  ```

`map<string,string>`类型的定义：

```json
{
  "type": "map",
  "value": [
    {
      "key": {
        "type": "string",
        "value": "map key",
        "tag": 0
      },
      "value": {
        "type": "string",
        "value": "map value",
        "tag": 1
      }
    }
  ],
  "tag": 0
}
```

`vector`类型的定义，除8大基本类型的数组以外，区分基本类型的`vector`和tars struct的`vector`是为了，在使用较常使用的基本类型`vector`时，`json`结构更为清晰简洁：

```json
{
  "type": "vector",
  "value": [
    {
      "type": "tars",
      "value": "more detail tars struct {}",
      "tag": 0
    },
    {
      "type": "tars",
      "value": "more detail tars struct {}",
      "tag": 0
    }
  ],
  "tag": 0
}
```

tars混合结构示意，对于如下Tars Sturct和tars Interface的IDL定义文件:

```cpp
module Tars2JsonExample
{
  enum EOpType
  {
    EOpType_None = 0,
    EOpType_SmsAuth = 1,
  };
  
  struct SubStruct
  {
    0 optional string sub;
  };
  
  struct TarsStructExample
  {
    0 optional vector<string> stringVec;
    1 optional long tryLoginTime = 0;
    2 optional EOpType enumTest;
    3 optional vector<map<string,SubStruct>> vectorMap;
    4 optional map<int, vector<byte> > mapCheck;
  };
  
  interface Tars2JsonExampleServant
  {
    int getTarsStruct(TarsStructExample resp);
  };
}; 
```

可以通过tars2json自动转化为：

```json
{
  "stringVec": {
    "type": "string[]",
    "value": [ "" ],
    "tag": 0
  },
  "tryLoginTime": {
    "type": "long",
    "value": 0,
    "tag": 1
  },
  "enumTest": {
    "type": "int",
    "value": 0,
    "tag": 2
  },
  "vectorMap": {
    "type": "vector",
    "value": [
      {
        "type": "map",
        "value": [
          {
            "key": {
              "type": "string",
              "value": "",
              "tag": 0
            },
            "value": {
              "type": "tars",
              "value": {
                "sub": {
                  "type": "string",
                  "value": "",
                  "tag": 0
                }
              },
              "tag": 1
            }
          }
        ],
        "tag": 0
      }
    ],
    "tag": 3
  },
  "mapCheck": {
    "type": "map",
    "value": [
      {
        "key": {
          "type": "int",
          "value": 0,
          "tag": 0
        },
        "value": {
          "type": "byte[]",
          "value": [ 0 ],
          "tag": 1
        }
      }
    ],
    "tag": 4
  }
}
```

### Tars2Json使用

您在使用插件时，并不需要手工地编写`json`格式的tars数据，插件中集成的tars2json工具会自动识别您定义的tars接口文件，完成转换，您只需：

1. 选择接口参数或返回值类型；
2. 如为tars，点击插件界面底部的tars2json button；
3. 指定"目标Tars接口名称"，如`TarsStructExample`；
4. 选择"目标Tars所在路径"，可以是Tars文件所在的目录路径，也可以是Tars文件的文件路径；
5. 点击button tars2json，即可获得对应Tars Struct 的json数据描述。


## JMeter压测集群部署建议

JMeter有一个广为人知的缺点：它的经典模型每个用户就是一个Java Thread，在线程调度方面，资源耗用是比较大的，这将导致单台部署JMeter的压测机器输出性能可能不会太高。

但，JMeter支持分布式压测，通过如下图的组网，可伸缩扩展JMeter slave个数，轻松满足各个量级的压力需求。

![JMeter分布式组网图](./res/network.png)

同时可以使用JMeter压测集群K8s部署方案来更加方便的管理压测集群：https://github.com/kubernauts/jmeter-kubernetes

## JMeter报表建议

无论是否使用K8s，当您搭建完JMeter + Influxdb + Grafana经典组网的压测环境后，您可以使用如下的Grafana dashboard来完成性能可视化监控。

Apache JMeter Dashboard using Core InfluxdbBackendListenerClient: https://grafana.com/grafana/dashboards/5496

This dashboard requires Apache JMeter 5 and upper versions. It shows overall statistics and you can zoom on one particular transaction. In order to use it you need to use JMeter Backend Listener and select InfluxdbBackendListenerClient.

Setup:

- Add Backend Listener to your test plan (Add -> Listener -> Backend Listener) and select org.apache.jmeter.visualizers.backend.influxdb.HttpMetricsSender
- Provide in the Parameters table the InfluxDB settings, provide a name for the test, and specify which samplers to record.

For more details, see this :

- https://jmeter.apache.org/usermanual/component_reference.html#Backend_Listener
- https://jmeter.apache.org/usermanual/realtime-results.html
