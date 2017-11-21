class Operator(object):
    def _getReadme(owner,repo,auth=None):
        import requests
		
		
		#Link with GitHub API
        get_url = ''.join(["https://api.github.com/repos/",owner,"/",repo,"/readme"])
        r = requests.get(get_url,auth=auth)
		
		#Fetch README file's URL
        if "download_url" in r.json():
            url = (r.json()['download_url'])
            r_readme = requests.get(url)
            return r_readme.text
        else:
            return None

	#Extract clickable URL within the README
    def getURL(owner,repo,auth=None):
        import re

        text = Operator._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"[^\!]\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

	#Extract picture
    def getPic(owner,repo,auth=None):
        import re

        text = Operator._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"\!\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

	
	#Fetch random project ID from GitHub
    def getProject(number,auth=None):
        import random
        import requests

        test = set()
        ret = []
		#Search the project ID within 1-70000000
        for i in range(number):
            id = random.randint(1, 70000000)
            while(id in test):
                id = random.randint(1, 70000000)
            url = ''.join(["https://api.github.com/repositories?since=",str(id)])
            r = requests.get(url,auth=auth)
            while(len(r.json())==0):
                id = random.randint(1, 70000000)
                while(id in test):
                    id = random.randint(1, 70000000)
                url = ''.join(["https://api.github.com/repositories?since=",str(id)])
                r = requests.get(url,auth=auth)

            ret.append([r.json()[0]["full_name"].split('/'),r.json()[0]['id']])
        return ret
	
	#Print result
    def getResult(number,auth=None):
        ret =[]

        pros = Operator.getProject(number=number,auth=auth)
        for pro in pros:
            ret1 = []
            ret1.append(Operator.getURL(owner=pro[0][0],repo=pro[0][1],auth=auth))
            ret1.append(Operator.getPic(owner=pro[0][0],repo=pro[0][1],auth=auth))
            proname = pro[0][0]+'/'+pro[0][1]
            ret.append([pro[1],proname,ret1])
        return ret
