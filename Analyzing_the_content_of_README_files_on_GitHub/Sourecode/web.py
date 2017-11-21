import op

from flask import Flask, jsonify, render_template, request
from flask_bootstrap import Bootstrap

render_template
app = Flask(__name__)
Bootstrap(app)

@app.route("/<int:number>")
def newmain(number):
    # auth=(username,password)
    operator = op.Operator()
    pros = operator.getResult(number, ('a1699186','1699186a'))
    
    return render_template('main.html', pros=pros)

@app.route('/_add_numbers')
def add_numbers():
	a = request.args.get('a')
	
	return jsonify(result=a)
	
	

@app.route("/")
def main():
    pros = []
    return render_template('main.html', pros=pros)

if __name__ == "__main__":
    app.run()
