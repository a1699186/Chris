class Operator(object):

    global wholeHeadings
    wholeHeadings = []

    def _getReadme(self, name,auth=None):
        import requests
        
        get_url = ''.join(["https://api.github.com/repos/",name,"/readme"])

        # user_agent = {'User-agent': 'Awesome-Octocat-App'}

        r = requests.get(get_url,auth=('a1699186', '1699186a'))

        # return r
        if "download_url" in r.json():
            url = (r.json()['download_url'])
            r_readme = r = requests.get(url)

            return r_readme.text
        else:
            return None

    def getURL(self, text,auth=None):
        import re

        if not text:
            return None

        finding = re.findall(r"[^\!]\[[\s\S]*?\]\([\s\S]*?\)",text)
        return len(finding)
        # return text

    def getPic(self, text,auth=None):
        import re

        # text = self._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"\!\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

    def getHeading(self, text,auth=None):
        import re

       
        if not text:
            return None

        finding = re.findall(r"[\#]+[\s]+[\s\S]*?\n",text)

        # need further process, e.g. [heading](http....)
        return len(finding)

    def getProject(self, number,auth=None):
        import random
        import requests

        test = set()
        ret = []
        for i in range(number):
            id = random.randint(1, 70000000)
            url = ''.join(["https://api.github.com/repositories?since=",str(id)])
            r = requests.get(url,auth=auth)

            ret.append([r.json()[0]["full_name"].split('/'),r.json()[0]['id']])
        return ret

    def getHeadingContents(self, text,auth=None):
        global wholeHeadings
        
        import re
        if not text:
            return None
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
       

    def getResult(self, number=None,auth=None):
        global wholeHeadings
        wholeHeadings = []
        emptyFile = 0;
        ret =[]
        pros = self.getProject(number=number,auth=auth)

        for pro in pros:
            name=pro[0][0]+'/'+pro[0][1]
            text = self._getReadme(name)
            heading = self.getHeading(text,auth=auth)
            if heading == None:
                emptyFile = emptyFile+1
            ret1 = {
                    'id':pro[1],
                    'name':name,
                    'url': self.getURL(text,auth=auth),
                    'pic': self.getPic(text,auth=auth),
                    'heading':heading,
                    'headingContent':self.getHeadingContents(text,auth=auth)
                   }
            ret.append(ret1) 

       
        # ret2 = {}
        ret3 = []
        for heading in wholeHeadings:

            myNum = wholeHeadings.count(heading)
            ele = "<td>"+str(heading).encode()+"</td><td>"+str(myNum).encode()+"</td>"

            if ele in ret3:
                continue
            else:
                ret3.append(ele)
        ret3.append("<td>empty file</td><td>"+str(emptyFile).encode()+"</td>")
        ret.append(ret3)        

        return ret
