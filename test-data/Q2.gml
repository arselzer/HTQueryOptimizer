graph [

  directed 0

  node [
    id 11
    label "{customer, orders} {N, C, O}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 12
    label "{supplier, lineitem} {N, S, O, D, E}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  edge [
    source 11
    target 12
  ]


]
