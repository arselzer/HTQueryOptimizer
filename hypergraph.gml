graph [

  directed 0

  node [
    id 1
    label "{t1, t6}    {X3, X12, X17}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 2
    label "{t4, t5}    {X0, X1, X3}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 3
    label "{t9, t10}    {X3, X12, X9}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 4
    label "{t7, t8}    {X3, X9, X18}"
    vgj [
      labelPosition "in"
      shape "Rectangle"
    ]
  ]

  node [
    id 5
    label "{t2, t3}    {X12, X5, X17}"
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
    source 1
    target 3
  ]

  edge [
    source 3
    target 4
  ]

  edge [
    source 1
    target 5
  ]

]
