class Operator(object):

    global wholeHeadings
    wholeHeadings = []

    def _getReadme(self, name,auth=None):
        import requests
        
		#Link with GitHub API
        get_url = ''.join(["https://api.github.com/repos/",name,"/readme"])

        print("getting the api from here")
        # user_agent = {'User-agent': 'Awesome-Octocat-App'}

        r = requests.get(get_url,auth=('a1699186', '1699186a'))

        # return r
        if "download_url" in r.json():
            url = (r.json()['download_url'])
            r_readme = r = requests.get(url)
            r_readme.text.replace("[","");
            r_readme.text.replace("]","");
            return r_readme.text
        else:
            return print("Empty")

	
    def getURLNum(self, text,auth=None):
        import re

        if not text:
            return print("Empty")
        #Amended here

        myfinding = []
        findings = re.findall(r"[^\!]\[[\s\S]*?\]\([\s\S]*?\)",text)
        for finding in findings:

            newfinding = re.findall(r"[a-zA-z]+://[^\s\)]*",finding)
            if len(newfinding)>0:
                myfinding.append(newfinding[0])
        
        return len(myfinding)

	#Extract clickable URL within the README
    def getURLContents(self, text,auth=None):
        import re

        if not text:
            return print("Empty")
        # Amended here

        myfinding = []
        findings = re.findall(r"[^\!]\[[\s\S]*?\]\([\s\S]*?\)",text)
        for finding in findings:

            newfinding = re.findall(r"[a-zA-z]+://[^\s\)]*",finding)
            if len(newfinding)>0:
                myfinding.append(newfinding[0])
        
        return myfinding

        #finding = re.findall(r"[a-zA-z]+://[^\s\)]*",text)
        

        # return finding

    
	#Extract clickable picture within the README
    def getPic(self, text,auth=None):
        import re
        import re

        # text = self._getReadme(owner=owner,repo=repo)
        if not text:
            return print("Empty")
        #Amended here

        finding = re.findall(r"\!\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

	# Extract heading
    def getHeading(self, text, auth=None):
        import re

        if not text:
            return print("Empty")
        #Amended here

        text = re.sub(r"```[^ƒ]*```","", text)
        finding = re.findall(r"[\#]+[\s]+[\s\S]*?\n",text)

        # need further process, e.g. [heading](http....)
        return len(finding)

	#Fetch random project ID from GitHub
    def getProject(self, number,auth=None):
        import random
        import requests

        test = set()
        ret = []
		
		#Search the project ID within 1-70000000
        for i in range(number):
            id = random.randint(1, 70000000)
            url = ''.join(["https://api.github.com/repositories?since=",str(id)])
            r = requests.get(url,auth=auth)

            ret.append([r.json()[0]["full_name"].split('/'),r.json()[0]['id']])
        return ret

	#Aggregate the section heading. Create the list
    def getHeadingContents(self, text,auth=None):
        global wholeHeadings
        
        import re
        if not text:
            return None

        text = re.sub(r"```[^ƒ]*```","", text)

        contents = re.findall(r"[\#]+[\s]+[\s\S]*?\n",text)

        if len(contents) == 0:
            return 0
        else:
            for num in range(0,len(contents)): 
                a = contents[num].replace("#", "")
                b = a.replace("\n", "")
                c = b.replace("&(.*);", "")
                wholeHeadings.append(c) 

        return wholeHeadings
       
    def getOneProject(self,auth=None):
        import random
        import requests

        test = set()
        ret = []
        
        id = random.randint(1, 70000000)
        url = ''.join(["https://api.github.com/repositories?since=",str(id)])
        r = requests.get(url,auth=auth)

        result = [r.json()[0]["full_name"].split('/'),r.json()[0]['id']]
        return result  

	#Print result		
    def getResult(self, number=None,auth=None):
        global wholeHeadings
        wholeHeadings = []
        emptyFile = 0;
        ret =[]
        pros = self.getProject(number=number,auth=auth)
        count = 0



        sum = 0
        for pro in pros:
            count = count + 1
            
            while True:
                pro = self.getOneProject(auth=auth)
                name=pro[0][0]+'/'+pro[0][1]
                text = self._getReadme(name)
                if type(text) is str:
                    if text.find("--\n") == -1:
                        if text.find("==\n") == -1:
                            break



            heading = self.getHeading(text,auth=auth)
            if heading == None:
                emptyFile = emptyFile+1
            else:
                sum = sum + heading
            ret1 = {
                    'no':count,
                    'id':pro[1],
                    'name':name,
                    'urlNum': self.getURLNum(text,auth=auth),
                    'pic': self.getPic(text,auth=auth),
                    'heading':heading,
                    'headingContent':self.getHeadingContents(text,auth=auth),
                    'urlcontents':self.getURLContents(text,auth=auth)
                   }
            ret.append(ret1) 

       

        ret3 = []
        for i in range(50):
            for heading in wholeHeadings:

                myNum = wholeHeadings.count(heading)
                ele = "<td>"+str(heading)+"</td><td>"+str(myNum)+"</td>"

                if ele in ret3:
                    continue
                else:
                    if myNum==i:
                        ret3.append(ele)
            # if myNum==i: 
                # print(emptyFile)
                # ret3.append("<td>empty file</td><td>"+str(emptyFile)+"</td>")
                # ret.append(ret3)        
        print(emptyFile)
        ret3 = ret3[::-1]
        ret3.append("<td><b>SUM of TOTAL SECTION HEADINGS</b></td><td>"+str(sum)+"</td>")
        ret3.append("<td><b>TOTAL EMPTY FILES EXISTED</b></td><td>"+str(emptyFile)+"</td>")


        ret.append(ret3) 
        
        return ret
