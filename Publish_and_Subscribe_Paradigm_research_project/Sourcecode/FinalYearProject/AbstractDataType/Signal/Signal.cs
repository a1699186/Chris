using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson;
using FinalYearProject.AbstractDataType.LinkedQueue;
using FinalYearProject.Models;

namespace FinalYearProject.AbstractDataType.Signal
{
    public class Signal : SignalInterface
    {
        public const int PUBLISH_EVENT = 1;
        public const int SUBSCRIBE_EVENT = 2;

        private int signalType;
        private LinkedQueueInterface<Object> key;

        public Signal()
        {
            signalType = PUBLISH_EVENT;
            key = new LinkedQueue<Object>();
        }



        public void setSignalType(int signalType)
        {
            if (this.hasKey())
            {
                throw new SignalADTException("please make sure you set the signal key first before you add key");
            }
            this.signalType = signalType;
        }




        public void addKey(Subscription key)
        {
            if (this.signalType != SUBSCRIBE_EVENT)
            {
                throw new SignalADTException("please ensure the event key and type is consistent");
            }

            this.key.enqueue(key);
        }


        public void addKey(Publication key)
        {
            if (this.signalType != PUBLISH_EVENT)
            {
                throw new SignalADTException("please ensure the event key and type is consistent");
            }

            this.key.enqueue(key);
        }





        public int getSignalType()
        {
            return this.signalType;
        }

        public Boolean hasKey()
        {
            return (!this.key.isEmpty());
        }

        public Object takeKey()
        {
            return this.key.dequeue();
        }



    }
}