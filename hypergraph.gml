graph [

  directed 0

  node [
    id 1
    label "{t3, t2}    {X12, X7, X5}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 2
    label "{t1, t9}    {X16, X12, X7}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 3
    label "{t10, t9}    {X16, X12, X9}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 4
    label "{t8, t7}    {X16, X9, X18}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 5
    label "{t5, t4}    {X0, X16, X1}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  edge [
    source 1
    target 2
  ]

  edge [
    source 2
    target 3
  ]

  edge [
    source 3
    target 4
  ]

  edge [
    source 2
    target 5
  ]

]
