reactor模式是什么?


传统的jdk的io模型  :  bio -> nio -> aio -> reactor
netty的io模型 :    单reacotr单线程模型-> 单reactor多线程模型 -> 主从reactor多线程模型
