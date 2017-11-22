using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.Collections.ObjectModel;
using FinalYearProject.Models.StockAPI;
using FinalYearProject.Controllers;
using FinalYearProject.Models;
using FinalYearProject.Models.Topic;
using FinalYearProject.DataAccessLayer;
using System.Configuration;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using System.Linq.Expressions;
using FinalYearProject.AbstractDataType.Signal;
using FinalYearProject.Class.PublishModule;

namespace FinalYearProject.Class.SubscribeModule
{
   
    public class YahooStockEngine
    {
        public YahooStockEngine() { }

        private const string BASE_URL = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20({0})&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        public void Fetch(ObservableCollection<yahooAPI> quotes)
        {
            string symbolList = String.Join("%2C", quotes.Select(w => "%22" + w.Symbol + "%22").ToArray());
            string url = string.Format(BASE_URL, symbolList);

            XDocument doc = XDocument.Load(url);
            Parse(quotes, doc);
        }

        private void Parse(ObservableCollection<yahooAPI> quotes, XDocument doc)
        {
            
            XElement results = doc.Root.Element("results");

            foreach (yahooAPI quote in quotes)
            {
                XElement q = results.Elements("quote").First(w => w.Attribute("symbol").Value == quote.Symbol);

                
                
                quote.LastTradePrice = GetDecimal(q.Element("LastTradePriceOnly").Value);

                IMongoQuery query = Query.EQ("topic.name",quote.Symbol);
                PublicationDAL publicationDAL = new PublicationDAL();
                List<Publication> foundList = publicationDAL.findPublication(query);

                if (foundList.Any())
                {
                    foreach (Publication element in foundList)
                    {
                        this.updatePublicationValue(element.publicationID, quote.Symbol, quote.LastTradePrice.ToString());
                    }
                }
                else
                {                                     
                    this.storePublicationValue(quote.Symbol, quote.LastTradePrice.ToString());
                }
            
            }
        }


        private void storePublicationValue(string stockName, string stockValue)
        {
            UserDAL userDal = new UserDAL();

            Publication newPublication = new Publication();

            User publisher = new User();
            IMongoQuery query = Query.EQ("user_name", "Yahoo Finance");
            List<User> userlist = userDal.findUser(query);
       
            
            foreach (User user in userlist)
            {
                publisher.userID = user.userID;
            }
                  
            MongoDBRef publisherRef = new MongoDBRef("UserCollection", publisher.userID);

            newPublication.publisher = publisherRef;

            StockPrice stockPrice = new StockPrice();
            stockPrice.name = stockName;    //"Dell"
            stockPrice.price = stockValue;  //"56.98"
            newPublication.topic = stockPrice;

            PublicationDAL dal = new PublicationDAL();
            dal.createPublication(newPublication);
        }


        private void updatePublicationValue(Guid publicationID, String stockName, String stockValue)
        {
            UserDAL userDal = new UserDAL();

            Publication updatePublication = new Publication();

            User publisher = new User();
            IMongoQuery query = Query.EQ("user_name", "Yahoo Finance");
            List<User> userlist = userDal.findUser(query);

            foreach (User user in userlist)
            {
                publisher.userID = user.userID;
            }

            MongoDBRef publisherRef = new MongoDBRef("UserCollection", publisher.userID);

            updatePublication.publicationID = publicationID;
            updatePublication.publisher = publisherRef;

            StockPrice stockPrice = new StockPrice();
            stockPrice.name = stockName;    //"Dell"
            stockPrice.price = stockValue;  //"56.98"
            updatePublication.topic = stockPrice;

            PublicationDAL dal = new PublicationDAL();
            dal.updatePublication(updatePublication);

            SignalInterface signal = new Signal();
            signal.setSignalType(Signal.PUBLISH_EVENT);
            signal.addKey(updatePublication);

            Command signalCommand = new AnalyseSignalCommand(signal);
            signalCommand.execute();
        }

        

        private decimal? GetDecimal(string input)
        {
            if (input == null) return null;

            input = input.Replace("%", "");

            decimal value;

            if (Decimal.TryParse(input, out value)) return value;
            return null;
        }

        private DateTime? GetDateTime(string input)
        {
            if (input == null) return null;

            DateTime value;

            if (DateTime.TryParse(input, out value)) return value;
            return null;
        }
    }
}