import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.tools.attach.*;

public class CmdLineTool
{
  static final String CONNECTOR_ADDRESS =
      "com.sun.management.jmxremote.localConnectorAddress";

  public static void main(String[] args)
  {
    if(args.length!=1)
      System.err.println("Usage: java CmdLineTool <pid>");
    else if(printStats(args[0])) return;
    System.out.println("Currently running");
    for(VirtualMachineDescriptor vmd:VirtualMachine.list())
      System.out.println(vmd.id()+"\t"+vmd.displayName());
  }

  private static boolean printStats(String id)
  {
    try
    {
      VirtualMachine vm=VirtualMachine.attach(id);
      System.out.println("Connected to "+vm.id());
      System.out.println("System Properties:");
      for(Map.Entry<?,?> en:vm.getSystemProperties().entrySet())
        System.out.println("\t"+en.getKey()+" = "+en.getValue());
      System.out.println();
      try
      {
        MBeanServerConnection sc=connect(vm);
        MemoryMXBean memoryMXBean = newPlatformMXBeanProxy(sc, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        System.out.println();
        getRamInfoHtml(memoryMXBean);
      } catch(AgentLoadException | AgentInitializationException ex)
      {
        System.out.println("JMX: "+ex);
      }
      vm.detach();
      return true;
    } catch(AttachNotSupportedException | IOException ex)
    {
      ex.printStackTrace();
    }
    return false;
  }
  static MBeanServerConnection connect(VirtualMachine vm)
    throws AgentLoadException, AgentInitializationException, IOException
  {
    String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
    if(connectorAddress == null)
    {
      System.out.println("loading agent");
      Properties props = vm.getSystemProperties();
      String home  = props.getProperty("java.home");
      String agent = home+File.separator+"lib"+File.separator+"management-agent.jar";
      vm.loadAgent(agent);
      connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
      while(connectorAddress==null) try {
        Thread.sleep(1000);
        connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
      } catch(InterruptedException ex){}
    }
    JMXConnector c=JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
    return c.getMBeanServerConnection();
  }
  static void getRamInfoHtml(MemoryMXBean memoryMXBean)
  {
    System.out.print("Heap:\t");
    MemoryUsage mu=memoryMXBean.getHeapMemoryUsage();
    System.out.println("allocated "+mu.getCommitted()+", used "+mu.getUsed()+", max "+mu.getMax());
    System.out.print("Non-Heap:\t");
    mu=memoryMXBean.getNonHeapMemoryUsage();
    System.out.println("allocated "+mu.getCommitted()+", used "+mu.getUsed()+", max "+mu.getMax());
    System.out.println("Pending Finalizations: "+memoryMXBean.getObjectPendingFinalizationCount());
  }
}
