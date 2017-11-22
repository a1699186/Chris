using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

using MongoDB.Driver.Builders;
using MongoDB.Driver;
using FinalYearProject.DataAccessLayer;

using FinalYearProject.AbstractDataType.Signal;
using FinalYearProject.Models;
using FinalYearProject.Models.Topic;
using FinalYearProject.Models.Expression;


namespace FinalYearProject.Class.PublishModule
{   
    public class AnalyseSignalCommand: Command
    {
        private delegate void AnalysekeyDelegate(Object key);
        private SignalInterface handledSignal = new Signal();

        public AnalyseSignalCommand(SignalInterface pSignal)
        {
            this.handledSignal = pSignal;
        }

        public void execute()
        {
            if (handledSignal != null)
            {
                if (handledSignal.getSignalType() == Signal.SUBSCRIBE_EVENT)
                {
                    while (handledSignal.hasKey())
                    {                        
                        AnalysekeyDelegate tmpAsynchronousMethod =
                            new AnalysekeyDelegate(analyseSubscribeEventKey);
                        tmpAsynchronousMethod.BeginInvoke(handledSignal.takeKey(),null,null);
                    }
                }
                else if (handledSignal.getSignalType() == Signal.PUBLISH_EVENT)
                {
                    while (handledSignal.hasKey())
                    {
                        AnalysekeyDelegate tmpAsynchronousMethod =
                            new AnalysekeyDelegate(analysePublishEventKey);
                        tmpAsynchronousMethod.BeginInvoke(handledSignal.takeKey(),null, null);
                    }
                }


            }//end null checking
        }//end method "excecute"



        private void analyseSubscribeEventKey(Object key)
        {
            if (key != null)
            {
                if (key is Subscription)
                {
                    Subscription subscriptionKey = (Subscription)key;

                    Command matchingCommand = new matchUsingSubscriptionKeyCommand(subscriptionKey);
                    matchingCommand.execute();
                }
            }

        }// end analyseSubscribeEventKey


        private void analysePublishEventKey(Object key)
        {
            if (key != null)
            {
                if (key is Publication)
                {
                    Publication publicationKey = (Publication)key;

                    Command matchingCommand = new matchUsingPublicationKeyCommand(publicationKey);
                    matchingCommand.execute();                  
                }
            }
        }// end analysePublishEventKey

    }
}