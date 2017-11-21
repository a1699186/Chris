from op import Operator as op

from flask import Flask, render_template

render_template
app = Flask(__name__)

@app.route("/<int:number>")
def newmain(number):
    # auth=(username,password)
    pros = op.getResult(number=number,auth=('a1699186','asdf1234!Q'))
    return render_template('main.html', pros=pros)


@app.route("/")
def main():
    pros = []
    return render_template('main.html', pros=pros)

if __name__ == "__main__":
    app.run()
