using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MongoDB.Bson;
using FinalYearProject.Models;


namespace FinalYearProject.AbstractDataType.Signal
{
    public interface SignalInterface
    {
        //cannot set Signal Type when consist key
        //to avoid key and type inconsitency
        //default signalType: Signal.PUBLISH_EVENT
        void setSignalType(int signalType); 


        //only accept two type of key instance
        //must consistent with event type
        // 1)   FinalYearProject.Models.Subscription(key) =  Signal.SUBSCRIBE_EVENT(event type) 
        // 2)   FinalYearProject.Models.Publication(key)  =  Signal.PUBLISH_EVENT(event type)
        void addKey(Subscription key);
        void addKey(Publication key);


        int getSignalType();
        Boolean hasKey();
        Object takeKey();
      
    }
}
