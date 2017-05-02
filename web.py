import op

from flask import Flask, render_template

render_template
app = Flask(__name__)

@app.route("/<int:number>")
def newmain(number):
    # auth=(username,password)
    operator = op.Operator()
    pros = operator.getResult(number, ('a1699186','1699186a'))
    return render_template('main.html', pros=pros)


@app.route("/")
def main():
    pros = []
    return render_template('main.html', pros=pros)

if __name__ == "__main__":
    app.run()
