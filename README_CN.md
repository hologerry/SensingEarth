# SensingEarth

感知地球：一个物联网应用系统，嵌入式计算系统课程设计。其包含两部分：Android 应用程序，CC3200 开发板。CC3200 开发板中的传感器采集环境中的数据，Android 应用程序展示用户关心的环境数据。

[For English](README.md)

## Features

* 与下位机 CC3200 开发版建立稳定的 Socket 网络连接
* 用户可以创建传感器配置来指定关心的传感器数据和其相应的阈值
* 向下位机发送传感器配置信息
* 实时获取传感器数据并用点图展示给用户
* 当检测到异常数据后警告用户，并给下位机发送异常反馈
* 用户可以随时更改传感器配置信息
* （部分的）Material Design

## TODO

* [ ] 完全的 Material Design: 添加 Material 变化动画和 Activity 切换动画

## Copyright

此系统 Android 应用程序部分由[我](https://github.com/eveouo)开发，CC3200 开发板程序由室友 @范坤杰 开发，保留所有权利。