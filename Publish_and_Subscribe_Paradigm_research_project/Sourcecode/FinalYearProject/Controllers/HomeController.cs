using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Net.Mail;
using FinalYearProject.Models.Topic;
using FinalYearProject.Models;
using FinalYearProject.DataAccessLayer;
using System.Configuration;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using System.Linq.Expressions;
using FinalYearProject.Models.Expression;
using FinalYearProject.Class.PublishModule;
using FinalYearProject.AbstractDataType.Signal;
using System.Net;
using System.IO;
using FinalYearProject.Class.SubscribeModule;
using FinalYearProject.Models.StockAPI;
using System.Web.Security;


namespace FinalYearProject.Controllers
{
    public class HomeController : Controller
    {

        public ActionResult Index()
        {
           /* this.addFakeSubscriber1();
            this.addFakeSubscriber2();
            this.addFakeSubscriber3();

            this.addfakeSubcription1();
            this.addfakeSubcription2();
            this.addfakeSubcription3();

            this.addFakePublisher();
            this.addFakePublication();

            Command publishCommand
                = new AnalyseSignalCommand(this.addFakeSignalForPublicationKey());
            publishCommand.execute();*/

            if (Request.IsAuthenticated)
            {
                return RedirectToAction("subscribeExpression"); 
            }

            return View();
        }


      /*  private void addFakeSubscriber1()
        {
            User user = new User();
            user.userID = new Guid("00000000-9999-9999-9999-000000000001");
            user.userName = "Subscriber1";
            user.password = "12345";
            UserDAL userDal = new UserDAL();
            userDal.CreateUser(user);
            System.Diagnostics.Debug.Write("userCreate");
        }

        private void addFakeSubscriber2()
        {
            User user = new User();
            user.userID = new Guid("11111111-9999-9999-9999-000000000001");
            user.userName = "Subscriber2";
            user.password = "12345";
            UserDAL userDal = new UserDAL();
            userDal.CreateUser(user);
            System.Diagnostics.Debug.Write("userCreate");
        }

        private void addFakeSubscriber3()
        {
            User user = new User();
            user.userID = new Guid("00000100-9999-9999-9999-100019000091");
            user.userName = "Subscriber3";
            user.password = "12345";
            UserDAL userDal = new UserDAL();
            userDal.CreateUser(user);
            System.Diagnostics.Debug.Write("userCreate");
        }
        




        //just for testing purpose
        private void addFakePublisher()
        {
            User user = new User();
            user.userID = new Guid("00000000-0000-0000-0000-000000000001");
            user.userName = "Getting Highland Stock";
            user.password = "12345";
            UserDAL userDal = new UserDAL();
            userDal.CreateUser(user);
            System.Diagnostics.Debug.Write("userCreate");
        }



        public void addfakeSubcription1()
        {
            Subscription subscription = new Subscription();

            Guid id = new Guid("00000000-9999-9999-9999-000000000001");
            MongoDBRef idref = new MongoDBRef(UserDAL.collectionName, id);
            subscription.subscriber = idref;

            StockPriceExpression se = new StockPriceExpression();
            se.firstFix = GenericExpression.FirstFixExpressionType.follow;
            se.prefixPrice = GenericExpression.PrefixExpressionType.moreThan;
            se.stockName = "AAPL";
            se.priceValue = "100";
            subscription.expression = se;

            NotificationChannel notificationChannel = new NotificationChannel();
            notificationChannel.email = "tanzm-wa11@student.tarc.edu.my";
            subscription.notificationChannel = notificationChannel;

            subscription.expiryDate = DateTime.UtcNow;

            SubscriptionDAL subscriptionDAL = new SubscriptionDAL();
            subscriptionDAL.createSubscription(subscription);       

        }

        public void addfakeSubcription2()
        {
            Subscription subscription = new Subscription();

            Guid id = new Guid("11111111-9999-9999-9999-000000000001");
            MongoDBRef idref = new MongoDBRef(UserDAL.collectionName, id);
            subscription.subscriber = idref;

            StockPriceExpression se = new StockPriceExpression();
            se.firstFix = GenericExpression.FirstFixExpressionType.follow;
            se.prefixPrice = GenericExpression.PrefixExpressionType.moreThan;
            se.stockName = "AAPL";
            se.priceValue = "50";
            subscription.expression = se;

            NotificationChannel notificationChannel = new NotificationChannel();
            notificationChannel.email = "tanzm-wa11@student.tarc.edu.my";
            subscription.notificationChannel = notificationChannel;

            subscription.expiryDate = DateTime.UtcNow;

            SubscriptionDAL subscriptionDAL = new SubscriptionDAL();
            subscriptionDAL.createSubscription(subscription);

        }

        public void addfakeSubcription3()
        {
            Subscription subscription = new Subscription();

            Guid id = new Guid("00000100-9999-9999-9999-100019000091");
            MongoDBRef idref = new MongoDBRef(UserDAL.collectionName, id);
            subscription.subscriber = idref;

            StockPriceExpression se = new StockPriceExpression();
            se.firstFix = GenericExpression.FirstFixExpressionType.follow;
            se.prefixPrice = GenericExpression.PrefixExpressionType.lessThan;
            se.stockName = "AAPL";
            se.priceValue = "30";
            subscription.expression = se;

            NotificationChannel notificationChannel = new NotificationChannel();
            notificationChannel.email = "tanzm-wa11@student.tarc.edu.my";
            subscription.notificationChannel = notificationChannel;

            subscription.expiryDate = DateTime.UtcNow;

            SubscriptionDAL subscriptionDAL = new SubscriptionDAL();
            subscriptionDAL.createSubscription(subscription);
        }

        public void addFakePublication()
        {
            Publication publication = new Publication();
            Guid id = new Guid("00000000-0000-0000-0000-000000000001");
            MongoDBRef idref = new MongoDBRef(UserDAL.collectionName, id);
            publication.publisher = idref;

            StockPrice topic  = new StockPrice();
            topic.name = "AAPL";
            topic.price = "20";
            publication.topic = topic;

            PublicationDAL publicationDAL = new PublicationDAL();
            publicationDAL.createPublication(publication);
        }


        private SignalInterface addFakeSignalForPublicationKey()
        {
            SignalInterface signal = new Signal();

            Publication publication = new Publication();
            Guid id = new Guid("00000000-0000-0000-0000-000000000001");
            MongoDBRef idref = new MongoDBRef(UserDAL.collectionName, id);
            publication.publisher = idref;

            StockPrice topic = new StockPrice();
            topic.name = "AAPL";
            topic.price = "20";
            publication.topic = topic;

            signal.addKey(publication);

            return signal;
        } */



        public ActionResult About()
        {
            return View();
        }

        public ActionResult SubscriberSignIn()
        {           
            return View();
        }

        [HttpPost]
        public ActionResult SubscriberSignIn(String userName, String password)
        {
            if (String.IsNullOrEmpty(userName) || String.IsNullOrEmpty(password))
            {
                return RedirectToAction("SubscriberSignIn");
            }


            User userModel = new User();
            if(userModel.IsValid(userName,password))
            {          
                FormsAuthentication.SetAuthCookie(userName,false);
                return RedirectToAction("subscribeExpression");               
            }
            else
            {
                return RedirectToAction("SubscriberSignIn");
            }
        }

        public ActionResult Logout()
        {
            FormsAuthentication.SignOut();
            return RedirectToAction("Index");
        }

       
        public ActionResult StockPriceCurrentValue()
        {
            if (!Request.IsAuthenticated)
            {
                return RedirectToAction("Index");
            }

            PublicationDAL publicationDAL = new PublicationDAL();
            UserDAL userDal = new UserDAL();

            IMongoQuery query = Query.EQ("user_name", "Yahoo Finance");
            List<User> userlist = userDal.findUser(query);
            User user = new User();

            foreach (User value in userlist)
            {
                user = value;
            }

            System.Diagnostics.Debug.WriteLine("testing2" + user.userID);

            query = Query.EQ("publisher.$id", user.userID);

            List<Publication> publicationList = publicationDAL.findPublication(query);
            List<StockPrice> stockPriceList = new List<StockPrice>();

            foreach (Publication value in publicationList)
            {
                System.Diagnostics.Debug.WriteLine("testing2");
                stockPriceList.Add((StockPrice)value.topic);
            }

            foreach (StockPrice element in stockPriceList)
            {
                System.Diagnostics.Debug.WriteLine("testing" + element.name);
            }

            return View(stockPriceList);
        }


        public ActionResult subscribeExpression()
        {
            if (!Request.IsAuthenticated)
            {
                return RedirectToAction("Index");
            }

            return View();
        }
        public ActionResult subscribeChooseExpression()
        {          
            return View();
        }
        public ActionResult SubscribedSuccessfully()
        {
            return View();
        }



        [HttpPost]
        public ActionResult SubscribedSuccessfully(String name, String firstfix, String prefixPrice, String PriceValue, String email, String phoneNumber) 
        {
            
            if(String.IsNullOrEmpty(name) || String.IsNullOrEmpty(prefixPrice) || String.IsNullOrEmpty(PriceValue) ||
                String.IsNullOrEmpty(email))
            {
                return RedirectToAction("stockPriceCurrentValue");
            }


            Subscription newSubscription = new Subscription();
            HttpCookie authCookie = Request.Cookies[FormsAuthentication.FormsCookieName];
            FormsAuthenticationTicket ticket = FormsAuthentication.Decrypt(authCookie.Value);

            //user ref
            User user = new User();
            IMongoQuery query = Query.EQ("user_name", ticket.Name);
            UserDAL userDal = new UserDAL();
            List<User> match = userDal.findUser(query);

            /*foreach (User element in match)
            {
                user = element;
            }*/

            if(match.Count > 0)
            {
                user = match.FirstOrDefault();
            }
            else
            {
                return RedirectToAction("stockPriceCurrentValue");
            }

            MongoDBRef subscriberRef = new MongoDBRef("UserCollection", user.userID);
            newSubscription.subscriber = subscriberRef;


            //expression
            StockPriceExpression stockPriceExpression = new StockPriceExpression();

            
            stockPriceExpression.firstFix = GenericExpression.FirstFixExpressionType.follow;
            if(firstfix.Equals("Not"))
            {
                stockPriceExpression.firstFix = GenericExpression.FirstFixExpressionType.notFollow;
            }


            if (prefixPrice.Equals("more than"))
            {
                stockPriceExpression.prefixPrice = GenericExpression.PrefixExpressionType.moreThan;
            }
            else if (prefixPrice.Equals("equal or less than"))
            {
                stockPriceExpression.prefixPrice = GenericExpression.PrefixExpressionType.equalOrLessThan;
            }
            else if (prefixPrice.Equals("equal of more than"))
            {
                stockPriceExpression.prefixPrice = GenericExpression.PrefixExpressionType.equalOrMoreThan;
            }
            else if (prefixPrice.Equals("less than"))
            {
                stockPriceExpression.prefixPrice = GenericExpression.PrefixExpressionType.lessThan;
            }


            stockPriceExpression.priceValue = PriceValue; 
            stockPriceExpression.stockName = name;

            newSubscription.expression = stockPriceExpression;


            NotificationChannel channnel = new NotificationChannel();
            channnel.email = email; 
            channnel.phoneNumber = phoneNumber; 

            newSubscription.notificationChannel = channnel;

            SubscriptionDAL subscriptionDAL = new SubscriptionDAL();
            subscriptionDAL.createSubscription(newSubscription);

            Signal signal = new Signal();
            signal.setSignalType(Signal.SUBSCRIBE_EVENT);
            signal.addKey(newSubscription);

            Command signalCommand = new AnalyseSignalCommand(signal);
            signalCommand.execute();

            System.Diagnostics.Debug.WriteLine("pass 1000");
            return RedirectToAction("SubscribedSuccessfully");
        }


        public ActionResult subscribeChooseStockName()
        {          
            return View();
        }


        public ActionResult notificationChannel()
        {
            return View();
        }



        public ActionResult Create()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Create(String userName, String password)
        {
            if(String.IsNullOrEmpty(userName) || String.IsNullOrEmpty(password))
            {
                return RedirectToAction("Create");
            }

            User newUser = new User();
            newUser.userName = userName;
            newUser.password = AbstractDataType.Helpers.SHA1.Encode(password);

            UserDAL user = new UserDAL();
            user.CreateUser(newUser);

            FormsAuthentication.SetAuthCookie(userName, false);
            return RedirectToAction("subscribeExpression");
        }

    }
      


 
}