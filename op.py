class Operator(object):
    def _getReadme(self, owner,repo,auth=None):
        import requests

        get_url = ''.join(["https://api.github.com/repos/",owner,"/",repo,"/readme"])
        r = requests.get(get_url,auth=auth)

        if "download_url" in r.json():
            url = (r.json()['download_url'])
            r_readme = r = requests.get(url)
            return r_readme.text
        else:
            return None

    def getURL(self, owner,repo,auth=None):
        import re

        text = self._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"[^\!]\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

    def getPic(self, owner,repo,auth=None):
        import re

        text = self._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"\!\[[\s\S]*?\]\([\s\S]*?\)",text)

        return len(finding)

    def getHeading(self, owner,repo,auth=None):
        import re

        text = self._getReadme(owner=owner,repo=repo)
        if not text:
            return None

        finding = re.findall(r"[\#]+[\s]+[\s\S]*?\n",text)

        # need further process, e.g. [heading](http....)
        return finding

    def getProject(self, number,auth=None):
        import random
        import requests

        test = set()
        ret = []
        for i in range(number):
            id = random.randint(1, 70000000)
            while(id in test):
                id = random.randint(1, 70000000)
            url = ''.join(["https://api.github.com/repositories?since=",str(id)])
            r = requests.get(url,auth=auth)
            while(len(r.json())==0):
                while(id in test):
                    id = random.randint(1, 70000000)
                url = ''.join(["https://api.github.com/repositories?since=",str(id)])
                r = requests.get(url,auth=auth)

            ret.append([r.json()[0]["full_name"].split('/'),r.json()[0]['id']])
        return ret

    def getResult(self, number=None,auth=None):
        ret =[]

        pros = self.getProject(number=number,auth=auth)
        for pro in pros:
            ret1 = []
            ret1.append(self.getURL(owner=pro[0][0],repo=pro[0][1],auth=auth))
            ret1.append(self.getPic(owner=pro[0][0],repo=pro[0][1],auth=auth))
            ret1.append(self.getHeading(owner=pro[0][0],repo=pro[0][1],auth=auth))
            proname = pro[0][0]+'/'+pro[0][1]
            ret.append([pro[1],proname,ret1])
        return ret
