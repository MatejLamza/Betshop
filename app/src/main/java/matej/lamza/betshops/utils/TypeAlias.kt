package matej.lamza.betshops.utils

import android.view.LayoutInflater
import android.view.ViewGroup

typealias ViewBindingInflate<VB> = (LayoutInflater, ViewGroup?, Boolean) -> VB
typealias ActivityViewBindingInflate<VB> = (LayoutInflater) -> VB

