using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using FinalYearProject.Models.Expression;
using FinalYearProject.DataAccessLayer;
using FinalYearProject.Models;
using FinalYearProject.Models.Topic;
using MongoDB.Driver;
using MongoDB.Driver.Builders;


namespace FinalYearProject.Class.PublishModule
{
    public class StartPublishProcessCommand : Command
    {
        
        private Subscription subscriptionKey;
        private List<Publication> matchingElement;

        private NotificationChannel channel = new NotificationChannel();
        private String receiverName = "Sorry, we got problem to identify your name";

        
        public StartPublishProcessCommand(Subscription subscriptionKey, List<Publication> matchingElement)
        {
            this.subscriptionKey = subscriptionKey;
            this.matchingElement = matchingElement;
        }

        public void execute()
        {
            System.Diagnostics.Debug.WriteLine("pass1");
            if(this.subscriptionKey != null && this.matchingElement != null)
            {
                System.Diagnostics.Debug.WriteLine("pass2");
                //identify susbcriber name
                UserDAL userDal = new UserDAL();
                IMongoQuery query = Query.EQ("_id",this.subscriptionKey.subscriber.Id);
                foreach (User user in userDal.findUser(query))
                {
                    System.Diagnostics.Debug.WriteLine("pass3");
                    this.receiverName = user.userName;
                }

                //identify channel
                this.channel = this.subscriptionKey.notificationChannel;

                //reorganize matchingElement and sending
                if (!String.IsNullOrEmpty(channel.email))
                {
                    System.Diagnostics.Debug.WriteLine("pass4");
                    String notificationMessage = this.reorganizeMatchedElementForMail();

                    Command sendMailCommand = new SendEmailCommand(this.channel.email, notificationMessage);
                    sendMailCommand.execute();
                }

                if (!String.IsNullOrEmpty(channel.phoneNumber))
                {
                    System.Diagnostics.Debug.WriteLine("pass5");
                    String notificationMessage = this.reorganizeMatchedElementForSMS();

                    Command sendSmsCommand = new SendSmsCommand(this.channel.phoneNumber, notificationMessage);
                    sendSmsCommand.execute();
                }

                System.Diagnostics.Debug.WriteLine("pass6");
            }
        }// end method "execute"

        

        private String reorganizeMatchedElementForMail()
        {
                String providerName;
                String mainBodyMessage = "";
                foreach (Publication element in this.matchingElement)
                {                
                    providerName = "<b>Sorry, we cannot identify the provider</b><br/>";
                    //find publisher name example: "Getting Highland stock"
                    UserDAL userDal = new UserDAL();
                    IMongoQuery query = Query.EQ("_id", element.publisher.Id);
                    foreach (User user in userDal.findUser(query))
                    {
                        providerName ="<b>Provider:</b>" + "  " + user.userName + "<br/>";
                    }

                   if(element.topic is StockPrice)
                   {
                       StockPrice stockPriceElement = (StockPrice)element.topic;
                       mainBodyMessage = mainBodyMessage + providerName + "<b>Stock Name:</b>" + " " 
                           + stockPriceElement.name + "; " + "<b>Price value:</b>"
                           + "  " + stockPriceElement.price + "<br/><br/><br/>";
                   }
                   else
                   {
                       mainBodyMessage = mainBodyMessage + providerName + "<b>sorry, service temporary unavailable</b><br/><br/><br/>";
                   }

                }// end loop

                return "<h3>Hi, " + receiverName + "<br/>" + "Be notice that the value below has match your expectation</h3>"
                    + "<br/><br/><br/>" + mainBodyMessage +
                    "<b>For more operation please</b> <a href= \"http://finalyearproject2305.azurewebsites.net/\">click here</a>";
        }



        private String reorganizeMatchedElementForSMS()
        {
            String providerName;
            String mainBodyMessage = "";
            foreach (Publication element in this.matchingElement)
            {
                providerName = "Sorry, we cannot identify the provider<br/>";
                //find publisher name example: "Getting Highland stock"
                UserDAL userDal = new UserDAL();
                IMongoQuery query = Query.EQ("_id", element.publisher.Id);
                foreach (User user in userDal.findUser(query))
                {
                    providerName = "Provider:" + "  " + user.userName + "\n";
                }

                if (element.topic is StockPrice)
                {
                    StockPrice stockPriceElement = (StockPrice)element.topic;
                    mainBodyMessage = mainBodyMessage + providerName + "Stock Name:"+ " " +stockPriceElement.name + "\n" 
                        + "Price value:" + "  " + stockPriceElement.price + "\n\n";
                }
                else
                {
                    mainBodyMessage = mainBodyMessage + providerName + "sorry, service temporary unavailable\n\n";
                }

            }// end loop

            return "Hi, " + receiverName + "\n" + "Be notice that the value below has match your expectation"
                + "\n\n" + mainBodyMessage +
                "For more operation please visit http://finalyearproject2305.azurewebsites.net";

        }


    }//end class

}