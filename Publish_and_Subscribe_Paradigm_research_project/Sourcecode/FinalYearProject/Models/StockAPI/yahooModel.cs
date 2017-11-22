using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Windows;
using System.Collections.ObjectModel;
using System.Threading;
using FinalYearProject.Models.StockAPI;
using FinalYearProject.Class.SubscribeModule;
using FinalYearProject.DataAccessLayer;
using MongoDB.Driver;
using MongoDB.Driver.Builders;



namespace FinalYearProject.Models.StockAPI
{
    public class yahooModel
    {

        public ObservableCollection<yahooAPI> Quotes { get; set; }

        public yahooModel()
        {
            Quotes = new ObservableCollection<yahooAPI>();

            Quotes.Add(new yahooAPI("AAPL"));
            Quotes.Add(new yahooAPI("MSFT"));
            Quotes.Add(new yahooAPI("INTC"));
            Quotes.Add(new yahooAPI("IBM"));
            Quotes.Add(new yahooAPI("RVBD"));
            Quotes.Add(new yahooAPI("AMZN"));
            Quotes.Add(new yahooAPI("BIDU"));
            Quotes.Add(new yahooAPI("SINA"));
            Quotes.Add(new yahooAPI("NVDA"));
            Quotes.Add(new yahooAPI("AMD"));
            Quotes.Add(new yahooAPI("WMT"));
            Quotes.Add(new yahooAPI("GLD"));
            Quotes.Add(new yahooAPI("SLV"));
            Quotes.Add(new yahooAPI("V"));
            Quotes.Add(new yahooAPI("MCD"));
            Quotes.Add(new yahooAPI("FAS"));


            UserDAL userDAL = new UserDAL();
            User publisher = new User();
            IMongoQuery query = Query.EQ("user_name", "Yahoo Finance");
            List<User> userlist = userDAL.findUser(query);


            if (!userlist.Any()) 
            { 
                User newUser = new User();
                newUser.userName = "Yahoo Finance";

                userDAL = new UserDAL();
                userDAL.CreateUser(newUser);
            }

           
            YahooStockEngine yahooEngine = new YahooStockEngine();
            yahooEngine.Fetch(Quotes);
          
        }
    }
}