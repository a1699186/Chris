using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ComponentModel;
using MongoDB.Bson.Serialization.Attributes;
using FinalYearProject.Class.SubscribeModule;


namespace FinalYearProject.Models.StockAPI
{
    public class yahooAPI : INotifyPropertyChanged
    {
            public event PropertyChangedEventHandler PropertyChanged;

            private string symbol;

            private decimal? lastTradePrice;





            public yahooAPI(string ticker)
            {
                symbol = ticker;
            }




            public decimal? LastTradePrice
            {
                get { return lastTradePrice; }
                set
                {
                    lastTradePrice = value;
                    if (PropertyChanged != null) PropertyChanged(this, new PropertyChangedEventArgs("LastTradePrice"));
                }
            }


            public string Symbol
            {
                get { return symbol; }
                set
                {
                    symbol = value;
                    if (PropertyChanged != null) PropertyChanged(this, new PropertyChangedEventArgs("Symbol"));
                }
            }
        }
}