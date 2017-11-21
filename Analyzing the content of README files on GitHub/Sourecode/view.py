import op
from django.shortcuts import render
from django.http import JsonResponse
def hello(request):
    operator = op.Operator()
    numStr = str(request.GET.get('num'))

    if numStr != 'None':
    	num = int(numStr)
    else:
    	num = 0
    pros = operator.getResult(num, ('a1699186','1699186a'))
    return render(request, 'main.html', {'pros': pros})

