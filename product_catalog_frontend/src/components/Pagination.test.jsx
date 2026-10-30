import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import Pagination from './Pagination'

describe('Pagination', () => {
  it('disables previous on first page and advances when requested', () => {
    const onChange = vi.fn()
    render(<Pagination page={0} totalPages={3} onChange={onChange} />)
    expect(screen.getByText('Página 1 de 3')).toBeInTheDocument()
    expect(screen.getByText('Anterior')).toBeDisabled()
    fireEvent.click(screen.getByText('Siguiente'))
    expect(onChange).toHaveBeenCalledWith(1)
  })
})
