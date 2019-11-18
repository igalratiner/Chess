import client.ServerClient


fun main(args: Array<String>) {
    //        while(true) {
    //            try {
    //                //sending the actual Thread of execution to sleep X milliseconds
    //                Thread.sleep(3000);
    //            } catch(InterruptedException ie) {}
    //            System.out.println("Hello from Engine!");
    //        }
    val serverClient = ServerClient()
    println(serverClient.sayHelloToMyself(Bla(b="d")))
}

