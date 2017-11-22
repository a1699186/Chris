using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.IdGenerators;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using FinalYearProject.DataAccessLayer;

namespace FinalYearProject.Models
{
    public class User
    {
        public User()
        {
            
        }

        [BsonId(IdGenerator = typeof(CombGuidGenerator))]
        public Guid userID { get; set; }


        [BsonElement("user_name")]
        public string userName { get; set; }


        [BsonElement("user_password")]
        public string password { get; set; }


        [BsonExtraElements]
        public BsonDocument extraElement { get; set; }

        
        public Boolean IsValid(string pUserName, string password)
        {
            if (string.IsNullOrEmpty(pUserName) || string.IsNullOrEmpty(password))
            {
                return false;
            }


            password = FinalYearProject.AbstractDataType.Helpers.SHA1.Encode(password);
            IMongoQuery query = Query.And(
                Query.EQ("user_name", pUserName),
                Query.EQ("user_password", password)
                );


            UserDAL userdal = new UserDAL();
            List<User> match = userdal.findUser(query);

            if (match.Count > 0)
            {
                return true;
            }

            return false;
        }

    }
}